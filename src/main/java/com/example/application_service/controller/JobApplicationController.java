package com.example.application_service.controller;

import com.example.application_service.dto.JobApplicationDto;
import com.example.application_service.enums.ApplicationStatus;
import com.example.application_service.models.JobApplication;
import com.example.application_service.service.JobApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/job-application")
public class JobApplicationController {

    @Autowired
    private JobApplicationService jobApplicationService;

    @PostMapping("/apply")
    public ResponseEntity<JobApplication> applyForJob(@RequestBody JobApplication jobApplication) {
        return ResponseEntity.ok(jobApplicationService.applyForJob(jobApplication));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<JobApplicationDto>> getApplicationsByUserId(@PathVariable Long userId) {
        List<JobApplicationDto> applications = jobApplicationService.getApplicationsByUserId(userId);
        return ResponseEntity.ok(applications);
    }

    @GetMapping("/job/{jobId}")
    public ResponseEntity<List<JobApplicationDto>> getApplicationsByJobId(@PathVariable Long jobId) {
        List<JobApplicationDto> applications = jobApplicationService.getApplicationsByJobId(jobId);
        return ResponseEntity.ok(applications);
    }

    @PatchMapping("/{applicationId}/status")
    public ResponseEntity<JobApplication> updateApplicationStatus(
            @PathVariable Long applicationId,
            @RequestParam ApplicationStatus status) {
        JobApplication updatedApplication = jobApplicationService.updateApplicationStatus(applicationId, status);
        return ResponseEntity.ok(updatedApplication);
    }

}
