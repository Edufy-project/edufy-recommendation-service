package com.example.edufy_recommendation_service.DTO;

import java.time.LocalDateTime;

public class UserFeedbackDTO {

    private Long id;
    private Long userId;
    private Long mediaId;
    private String mediaType;
    private String feedbackType;
    private LocalDateTime timestamp;

    public UserFeedbackDTO() {}
    public UserFeedbackDTO(Long id, Long userId, Long mediaId, String mediaType, String feedbackType, LocalDateTime timestamp) {
        this.id = id;
        this.userId = userId;
        this.mediaId = mediaId;
        this.mediaType = mediaType;
        this.feedbackType = feedbackType;
        this.timestamp = timestamp;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getMediaId() {
        return mediaId;
    }

    public void setMediaId(Long mediaId) {
        this.mediaId = mediaId;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public String getFeedbackType() {
        return feedbackType;
    }

    public void setFeedbackType(String feedbackType) {
        this.feedbackType = feedbackType;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
