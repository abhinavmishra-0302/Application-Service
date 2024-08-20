package com.example.application_service.models;

import java.io.Serializable;
import java.time.LocalDateTime;

public class NotificationMessage implements Serializable {
    private Long userId;

    private String title;

    private String message;

    private LocalDateTime createdAt;

    public NotificationMessage() {}

    public NotificationMessage(Long userId, String title, String message, LocalDateTime createdAt) {
        this.userId = userId;
        this.title = title;
        this.message = message;
        this.createdAt = createdAt;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
