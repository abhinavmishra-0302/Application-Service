package com.example.application_service.controller;

import com.example.application_service.models.Feedback;
import com.example.application_service.service.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/feedback")
public class FeedbackController {

    @Autowired
    private FeedbackService feedbackService;

    @PostMapping("/{applicationId}")
    public ResponseEntity<Feedback> addFeedback(
            @PathVariable Long applicationId,
            @RequestParam Long employerId,
            @RequestParam String comments) {
        Feedback feedback = feedbackService.addFeedback(applicationId, employerId, comments);
        return ResponseEntity.ok(feedback);
    }
}
