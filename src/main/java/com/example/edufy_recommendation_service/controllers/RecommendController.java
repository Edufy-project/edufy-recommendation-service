package com.example.edufy_recommendation_service.controllers;

import com.example.edufy_recommendation_service.entities.UserDTO;
import com.example.edufy_recommendation_service.services.RecommendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class RecommendController {

    private final RecommendService recommendService;

    @Autowired
    public RecommendController(RecommendService recommendService) {
        this.recommendService = recommendService;
    }

    @GetMapping("recommended/media/{userId}")
    public List<?> getRecommendedMediaByUserId(@PathVariable Long userId) {
        return recommendService.getRecommendedMediaListByUserId(userId);
    }

    @GetMapping("recommended/genre/{userId}")
    public List<String> getRecommendedGenreByUserId(@PathVariable Long userId) {
        return recommendService.getRecommendedGenreListByUserId(userId);
    }

}
