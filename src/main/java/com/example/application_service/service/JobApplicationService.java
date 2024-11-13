package com.example.application_service.service;

import com.example.application_service.dto.JobApplicationDto;
import com.example.application_service.enums.ApplicationStatus;
import com.example.application_service.models.JobApplication;
import com.example.application_service.models.NotificationMessage;
import com.example.application_service.repository.JobApplicationRepository;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class JobApplicationService {

    @Autowired
    private JobApplicationRepository jobApplicationRepository;

    @Autowired
    private WebClient.Builder webClientBuilder;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private DirectExchange exchange;

    private static final String USER_PROFILE_SERVICE_URL = "http://localhost:8082/api/v1/job-seeker-profiles/";
    private static final String JOB_SERVICE_URL = "http://localhost:8081/api/v1/jobs/";

    public static final String NOTIFICATION_QUEUE = "notification_queue";

    public String requestJobTitle(Long jobId) {
        rabbitTemplate.convertAndSend(exchange.getName(), "jobTitleRequestRoutingKey", jobId);

        Object response = rabbitTemplate.receiveAndConvert("jobTitleResponseQueue", 5000);
        return response != null ? response.toString() : "No response received";
    }

    public void sendNotification(Long userId, String title, String message) {
        NotificationMessage notificationMessage = new NotificationMessage(userId, title, message, LocalDateTime.now());
        rabbitTemplate.convertAndSend(NOTIFICATION_QUEUE, notificationMessage);
    }

    public JobApplication applyForJob(JobApplication jobApplication) {
        Mono<Object> jobResponse = webClientBuilder.build()
                .get()
                .uri(JOB_SERVICE_URL + jobApplication.getJobId())
                .retrieve()
                .bodyToMono(Object.class);

        Mono<Object> userResponse = webClientBuilder.build()
                .get()
                .uri(USER_PROFILE_SERVICE_URL + jobApplication.getUserId())
                .retrieve()
                .bodyToMono(Object.class);

        jobResponse.block();
        userResponse.block();

        JobApplication jobApply = new JobApplication();
        jobApply.setJobId(jobApplication.getJobId());
        jobApply.setUserId(jobApplication.getUserId());
        jobApply.setCoverLetter(jobApplication.getCoverLetter());
        jobApply.setResumeUrl(jobApplication.getResumeUrl());

        Long userId = jobApplication.getUserId();
        String title = "Successfully applied for job";
        String message = "You have successfully applied for the job with id: " + jobApplication.getJobId();

        sendNotification(userId, title, message);

        return jobApplicationRepository.save(jobApply);
    }

    public List<JobApplicationDto> getApplicationsByUserId(Long userId) {
        List<JobApplication> jobApplications = jobApplicationRepository.findByUserId(userId);

        List<JobApplicationDto> responseDTOList = new ArrayList<>();

        for (JobApplication jobApplication : jobApplications) {
            String jobTitle = requestJobTitle(jobApplication.getJobId());

            System.out.println("Application Service: "+jobApplication.getJobId());
            System.out.println("Application Service: "+jobTitle);

            JobApplicationDto responseDTO = new JobApplicationDto();
            responseDTO.setApplicationId(jobApplication.getId());
            responseDTO.setJobId(jobApplication.getJobId());
            responseDTO.setJobTitle(jobTitle);  // Add job title
            responseDTO.setUserId(jobApplication.getUserId());
            responseDTO.setStatus(jobApplication.getStatus());
            responseDTO.setResumeUrl(jobApplication.getResumeUrl());
            responseDTO.setCoverLetter(jobApplication.getCoverLetter());
            responseDTO.setAppliedAt(jobApplication.getAppliedAt());
            responseDTO.setFeedback(jobApplication.getFeedback() != null ? jobApplication.getFeedback() : null);

            responseDTOList.add(responseDTO);
        }
        return responseDTOList;

    }

    public List<JobApplicationDto> getApplicationsByJobId(Long jobId) {
        List<JobApplication> jobApplications = jobApplicationRepository.findByJobId(jobId);

        List<JobApplicationDto> responseDTOList = new ArrayList<>();

        for (JobApplication jobApplication : jobApplications) {
            String jobTitle = requestJobTitle(jobApplication.getJobId());

            System.out.println("Application Service: "+jobApplication.getJobId());
            System.out.println("Application Service: "+jobTitle);

            JobApplicationDto responseDTO = new JobApplicationDto();
            responseDTO.setApplicationId(jobApplication.getId());
            responseDTO.setJobId(jobApplication.getJobId());
            responseDTO.setJobTitle(jobTitle);  // Add job title
            responseDTO.setUserId(jobApplication.getUserId());
            responseDTO.setStatus(jobApplication.getStatus());
            responseDTO.setResumeUrl(jobApplication.getResumeUrl());
            responseDTO.setCoverLetter(jobApplication.getCoverLetter());
            responseDTO.setAppliedAt(jobApplication.getAppliedAt());
            responseDTO.setFeedback(jobApplication.getFeedback() != null ? jobApplication.getFeedback() : null);

            responseDTOList.add(responseDTO);
        }
        return responseDTOList;

    }


    public JobApplication updateApplicationStatus(Long applicationId, ApplicationStatus status){
        JobApplication jobApplication = jobApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        jobApplication.setStatus(status);
        return jobApplicationRepository.save(jobApplication);
    }
}
