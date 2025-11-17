package com.example.edufy_recommendation_service.services;

import com.example.edufy_recommendation_service.entities.MediaReferenceDTO;
import com.example.edufy_recommendation_service.entities.RecommendationDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RecommendService {

    private RestClient userServiceClient;
    private RestClient mediaServiceClient;

    public RecommendService(RestClient.Builder restClientBuilder,
                            @Value("http://localhost:9093") String userServiceUrl,
                            @Value("http://localhost:9091") String mediaServiceUrl)
    {
        this.userServiceClient = restClientBuilder
                .baseUrl(userServiceUrl)
                .build();

        this.mediaServiceClient = restClientBuilder
                .baseUrl(mediaServiceUrl)
                .build();
    }

    public List<MediaReferenceDTO> getUserMediaHistory(Long userId) {
        try {
            List<MediaReferenceDTO> serviceResponse = userServiceClient.get()
                    .uri("/api/edufy/usermediahistory/" + userId)
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<MediaReferenceDTO>>() {});

            return serviceResponse;

        } catch (Exception e) {
            return null;
        }
    }

    public List<RecommendationDTO> getRecommendedMediaListByUserId(String mediaType, Long userId) {

        List<MediaReferenceDTO> userHistory = getUserMediaHistory(userId);
        List<String> preferredGenres = getMediaGenresFromHistory(userHistory);
        List<RecommendationDTO> recommendationsList = new ArrayList<>();

        recommendationsList.addAll(getMediaByPreferredGenres(mediaType, userId, preferredGenres, 10));
        Collections.shuffle(recommendationsList);

        return recommendationsList;
    }

    public List<RecommendationDTO> getMediaByPreferredGenres(String mediaType, Long userId, List<String> preferredGenres, int amount) {
        try {
            int preferredAmount = (int) (amount * 0.8);

            List<MediaReferenceDTO> userHistory = getUserMediaHistory(userId);
            List<RecommendationDTO> recommendedMediaList = new ArrayList<>();

            List<String> shuffledGenreList = new ArrayList<>(preferredGenres);
            Collections.shuffle(shuffledGenreList);

            for (String genre : shuffledGenreList) {
                if (recommendedMediaList.size() > preferredAmount) {
                    break;
                }

                List<RecommendationDTO> recommendedMedia = mediaServiceClient.get()
                        .uri("/edufy/api/mediaplayer/getmedia/" + mediaType + "/" + genre.toLowerCase())
                        .retrieve()
                        .body(new ParameterizedTypeReference<List<RecommendationDTO>>() {});

                if (recommendedMedia != null) {
                    for (RecommendationDTO media : recommendedMedia) {
                        if (recommendedMediaList.size() > preferredAmount) {
                            break;
                        }
                        if (!isMediaPlayed(media.getId(), userHistory) && !isMediaAdded(media.getId(), recommendedMediaList)) {
                            recommendedMediaList.add(media);
                        }
                    }
                }

            }

            List<RecommendationDTO> allMedia = mediaServiceClient.get()
                    .uri("/edufy/api/mediaplayer/getmedia/all/" + mediaType)
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<RecommendationDTO>>() {});

            if (allMedia != null) {
                for (RecommendationDTO media : allMedia) {
                    if (recommendedMediaList.size() > amount) {
                        break;
                    }

                    if (!isMediaPlayed(media.getId(), userHistory) && !isMediaAdded(media.getId(), recommendedMediaList)) {
                        recommendedMediaList.add(media);
                    }
                }
            }

            Collections.shuffle(recommendedMediaList);
            return recommendedMediaList.stream().limit(amount).toList();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<String> getMediaGenresFromHistory(List<MediaReferenceDTO> userHistory) {
        Map<String, Integer> frequencyMap = new HashMap<>();
        List<String> genreList = new ArrayList<>();

        for (MediaReferenceDTO media : userHistory) {
            genreList.add(getMediaGenre(media.getMediaType(), media.getMediaId()));
        }

        for (String genre : genreList) {
            frequencyMap.put(genre, frequencyMap.getOrDefault(genre, 0) + 1);
        }

        return frequencyMap.entrySet().stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .map(e -> e.getKey())
                .toList();
    }

    public String getMediaGenre(String mediaType, Long mediaId) {
        try {
            return mediaServiceClient.get()
                    .uri("/edufy/api/mediaplayer/getgenre/" + mediaType + "/" + mediaId)
                    .retrieve()
                    .body(String.class);

        } catch (Exception e) {
            return null;
        }
    }

    private boolean isMediaPlayed(Long mediaId, List<MediaReferenceDTO> userHistory) {
        for (MediaReferenceDTO media : userHistory) {
            if (media.getMediaId().equals(mediaId)) {
                return true;
            }
        }
        return false;
    }

    private boolean isMediaAdded(Long mediaId, List<RecommendationDTO> recommendedList) {
        for (RecommendationDTO media : recommendedList) {
            if (media.getId().equals(mediaId)) {
                return true;
            }
        }
        return false;
    }

}
