package com.example.edufy_recommendation_service.controllers;

import com.example.edufy_recommendation_service.DTO.RecommendationDTO;
import com.example.edufy_recommendation_service.DTO.UserFeedbackDTO;
import com.example.edufy_recommendation_service.services.RecommendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RestController
@RequestMapping("edufy/api/recommended/")
public class RecommendController {

    private final RecommendService recommendService;
    private final RestTemplate restTemplate;

    @Autowired
    public RecommendController(RecommendService recommendService, RestTemplate restTemplate) {
        this.recommendService = recommendService;
        this.restTemplate = restTemplate;
    }

    @GetMapping("{mediaType}/{userId}")
    public ResponseEntity<List<RecommendationDTO>> getRecommendedMediaByUserId(@PathVariable String mediaType, @PathVariable Long userId) {
        return ResponseEntity.ok(recommendService.getRecommendedMediaListByUserId(mediaType, userId));
    }


    @GetMapping("getdislikes/{userId}")
    public List<UserFeedbackDTO> getUserFeedbackTEST(@PathVariable Long userId, @AuthenticationPrincipal Jwt jwt) {
        String token = jwt.getTokenValue();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<List<UserFeedbackDTO>> response = restTemplate.exchange(
                "http://localhost:9093/edufy/api/users/user/" + userId + "/feedback/dislikes",
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<List<UserFeedbackDTO>>() {}
        );

        return response.getBody();
    }

}
