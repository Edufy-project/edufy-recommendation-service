package com.example.edufy_recommendation_service.controllers;

import com.example.edufy_recommendation_service.DTO.RecommendationDTO;
import com.example.edufy_recommendation_service.services.RecommendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("edufy/api/recommended/")
public class RecommendController {

    private final RecommendService recommendService;

    @Autowired
    public RecommendController(RecommendService recommendService) {
        this.recommendService = recommendService;
    }

    @GetMapping("{mediaType}/{userId}")
    public List<RecommendationDTO> getRecommendedMediaByUserId(@PathVariable String mediaType, @PathVariable Long userId, @AuthenticationPrincipal Jwt jwt) {
        String token = jwt.getTokenValue();
        return recommendService.getRecommendedMediaListByUserId(mediaType, userId, token);
    }

}
