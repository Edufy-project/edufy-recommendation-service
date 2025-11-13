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
            String mostPlayedGenre = preferredGenres.getFirst();
            List<MediaReferenceDTO> userHistory = getUserMediaHistory(userId);

            List<RecommendationDTO> recommendedMedia = mediaServiceClient.get()
                    .uri("/api/edufy/mediaplayer/getmedia/" + mediaType + "/" + mostPlayedGenre.toLowerCase())
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<RecommendationDTO>>() {});

            if (recommendedMedia != null) {
                List<RecommendationDTO> filteredMediaList = new ArrayList<>();

                for (RecommendationDTO recommended : recommendedMedia) {
                    boolean played = false;

                    for (MediaReferenceDTO media : userHistory) {
                        if (media.getMediaId().equals(recommended.getId())) {
                            played = true;
                            break;
                        }
                    }

                    if (!played) {
                        filteredMediaList.add(recommended);
                    }
                }

                if (filteredMediaList.size() < amount) {
                    List<RecommendationDTO> allMedia = mediaServiceClient.get()
                            .uri("/api/edufy/mediaplayer/getmedia/all/" + mediaType)
                            .retrieve()
                            .body(new ParameterizedTypeReference<List<RecommendationDTO>>() {});

                    if (allMedia != null) {
                        for (RecommendationDTO media : allMedia) {
                            boolean added = false;
                            for (RecommendationDTO filteredMedia : filteredMediaList) {
                                if (filteredMedia.getId().equals(media.getId())) {
                                    added = true;
                                    break;
                                }
                            }

                            boolean played = false;
                            for (MediaReferenceDTO userMedia : userHistory) {
                                if (userMedia.getMediaId().equals(media.getId())) {
                                    played = true;
                                    break;
                                }
                            }

                            if (!added && !played) {
                                filteredMediaList.add(media);

                                if (filteredMediaList.size() == amount) {
                                    break;
                                }
                            }

                        }
                    }
                }

                Collections.shuffle(filteredMediaList);
                return filteredMediaList.stream().limit(amount).collect(Collectors.toList());

            } else {
                return null;
            }

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
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    public String getMediaGenre(String mediaType, Long mediaId) {
        try {
            return mediaServiceClient.get()
                    .uri("/api/edufy/mediaplayer/getgenre/" + mediaType + "/" + mediaId)
                    .retrieve()
                    .body(String.class);

        } catch (Exception e) {
            return null;
        }
    }

}
