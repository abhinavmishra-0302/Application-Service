package com.example.application_service.service;

import com.example.application_service.models.Feedback;
import com.example.application_service.repository.FeedbackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FeedbackService {

    @Autowired
    private FeedbackRepository feedbackRepository;

    public Feedback addFeedback(Long applicationId, Long employerId, String comments) {

        Feedback feedback = new Feedback();
        feedback.setEmployerId(employerId);
        feedback.setComments(comments);

        return feedbackRepository.save(feedback);
    }
}
