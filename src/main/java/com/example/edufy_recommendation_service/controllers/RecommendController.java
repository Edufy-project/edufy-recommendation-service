package com.example.edufy_recommendation_service.controllers;

import com.example.edufy_recommendation_service.DTO.RecommendationDTO;
import com.example.edufy_recommendation_service.services.RecommendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class RecommendController {

    private final RecommendService recommendService;

    @Autowired
    public RecommendController(RecommendService recommendService) {
        this.recommendService = recommendService;
    }

    @GetMapping("recommended/{mediaType}/{userId}")
    public List<RecommendationDTO> getRecommendedMediaByUserId(@PathVariable String mediaType, @PathVariable Long userId) {
        return recommendService.getRecommendedMediaListByUserId(mediaType, userId);
    }

    @GetMapping("getgenre/{mediaType}/{mediaId}")
    public String getGenre(@PathVariable String mediaType, @PathVariable Long mediaId) {
        return recommendService.getMediaGenre(mediaType, mediaId);
    }


    //todo REMOVE THIS METHOD REMOVE THIS METHOD REMOVE THIS METHOD REMOVE THIS METHOD REMOVE THIS METHOD REMOVE THIS METHOD
    @GetMapping("recommended/{userId}/stats")
    public Map<String, Integer> getRecommendedMediaStats(@PathVariable Long userId) {
        return recommendService.getRecommendedMediaStatsREMOVE(userId);
    }
    //todo REMOVE THIS METHOD REMOVE THIS METHOD REMOVE THIS METHOD REMOVE THIS METHOD REMOVE THIS METHOD REMOVE THIS METHOD

}
