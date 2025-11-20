package com.example.edufy_recommendation_service.DTO;

public class MediaReferenceDTO {

    private String mediaType;
    private Long mediaId;

    public MediaReferenceDTO() {}
    public MediaReferenceDTO(String mediaType, Long mediaId) {
        this.mediaType = mediaType;
        this.mediaId = mediaId;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public Long getMediaId() {
        return mediaId;
    }

    public void setMediaId(Long mediaId) {
        this.mediaId = mediaId;
    }
}
