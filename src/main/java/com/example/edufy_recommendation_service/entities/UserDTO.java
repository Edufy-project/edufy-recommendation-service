package com.example.edufy_recommendation_service.entities;

import java.util.List;

public class UserDTO {

    private Long id;
    private String username;
    private String password;
    private String preferredGenres;
    private Long totalPlayCount;
    private String roles;
    private List<?> mediaHistory;

    public UserDTO() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPreferredGenres() {
        return preferredGenres;
    }

    public void setPreferredGenres(String preferredGenres) {
        this.preferredGenres = preferredGenres;
    }

    public Long getTotalPlayCount() {
        return totalPlayCount;
    }

    public void setTotalPlayCount(Long totalPlayCount) {
        this.totalPlayCount = totalPlayCount;
    }

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }

    public List<?> getMediaHistory() {
        return mediaHistory;
    }

    public void setMediaHistory(List<?> mediaHistory) {
        this.mediaHistory = mediaHistory;
    }
}
