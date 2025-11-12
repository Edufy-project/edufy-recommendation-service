package com.example.edufy_recommendation_service.entities;

public class RecommendationDTO {

    private Long id;
    private String genre;
    private String title;

    public RecommendationDTO() {}
    public RecommendationDTO(Long id, String genre, String title) {
        this.id = id;
        this.genre = genre;
        this.title = title;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
