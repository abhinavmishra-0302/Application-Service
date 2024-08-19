package com.example.application_service.service;

import com.example.application_service.enums.ApplicationStatus;
import com.example.application_service.models.JobApplication;
import com.example.application_service.repository.JobApplicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class JobApplicationService {

    @Autowired
    private JobApplicationRepository jobApplicationRepository;

    @Autowired
    private WebClient.Builder webClientBuilder;

    private static final String USER_PROFILE_SERVICE_URL = "http://localhost:8082/api/v1/job-seeker-profiles/";
    private static final String JOB_SERVICE_URL = "http://localhost:8081/api/v1/jobs/";

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
        return jobApplicationRepository.save(jobApply);
    }

    public List<JobApplication> getApplicationsByUserId(Long userId) {
        return jobApplicationRepository.findByUserId(userId);
    }

    public List<JobApplication> getApplicationsByJobId(Long jobId) {
        return jobApplicationRepository.findByJobId(jobId);
    }

    public JobApplication updateApplicationStatus(Long applicationId, ApplicationStatus status){
        JobApplication jobApplication = jobApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        jobApplication.setStatus(status);
        return jobApplicationRepository.save(jobApplication);
    }
}
