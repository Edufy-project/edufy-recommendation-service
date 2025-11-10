package com.example.edufy_recommendation_service.entities;

public class RecommendationDTO {

    private Long mediaId;
    private String mediaType;
    private String title;

    public RecommendationDTO() {}
    public RecommendationDTO(Long mediaId, String mediaType, String title) {
        this.mediaId = mediaId;
        this.mediaType = mediaType;
        this.title = title;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
