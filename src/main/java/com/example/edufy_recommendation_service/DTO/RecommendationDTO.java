package com.example.edufy_recommendation_service.DTO;

public class RecommendationDTO {

    private Long id;
    private String genreName;
    private String title;

    public RecommendationDTO() {}
    public RecommendationDTO(Long id, String genreName, String title) {
        this.id = id;
        this.genreName = genreName;
        this.title = title;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGenreName() {
        return genreName;
    }

    public void setGenreName(String genreName) {
        this.genreName = genreName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
