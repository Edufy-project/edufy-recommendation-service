package com.example.edufy_recommendation_service.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
public class RecommendService {

    private RestClient restClient;

    public RecommendService(RestClient.Builder restClientBuilder, @Value("http://localhost:8080") String url) {
        this.restClient = restClientBuilder
                .baseUrl(url)
                .build();
    }

    public List<?> getRecommendedMediaListByUserId(Long userId) {
        try {

            List<?> serviceResponse = restClient.get()
                    .uri("/api/edufy/usermediahistory/" + userId)
                    .retrieve()
                    .body(List.class);

            return serviceResponse;

        } catch (Exception e) {
            return null;
        }
    }

    public List<String> getRecommendedGenreListByUserId(Long userId) {
        try {

            return null;

        } catch (Exception e) {
            return null;
        }
    }

}
