package com.example.edufy_recommendation_service.services;

import com.example.edufy_recommendation_service.DTO.MediaReferenceDTO;
import com.example.edufy_recommendation_service.DTO.RecommendationDTO;
import com.example.edufy_recommendation_service.DTO.UserFeedbackDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@Service
public class RecommendService {

    private RestClient userServiceClient;
    private RestClient mediaServiceClient;

    public RecommendService(RestClient.Builder restClientBuilder,
                            @Value("${user.service.url}") String userServiceUrl,
                            @Value("${media.service.url}") String mediaServiceUrl)
    {
        this.userServiceClient = restClientBuilder
                .baseUrl(userServiceUrl)
                .build();

        this.mediaServiceClient = restClientBuilder
                .baseUrl(mediaServiceUrl)
                .build();
    }

    public List<MediaReferenceDTO> getUserMediaHistory(Long userId, String token) {
        try {
            List<MediaReferenceDTO> serviceResponse = userServiceClient.get()
                    .uri("/edufy/api/users/usermediahistory/" + userId)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<MediaReferenceDTO>>() {});

            return serviceResponse;

        } catch (Exception e) {
            return null;
        }
    }

    public List<RecommendationDTO> getRecommendedMediaListByUserId(String mediaType, Long userId, String token) {
        List<MediaReferenceDTO> userHistory = getUserMediaHistory(userId, token);
        List<String> preferredGenres = getMediaGenresFromHistory(userHistory, getUserLikesById(userId, token), getUserDislikesById(userId, token), token);
        List<RecommendationDTO> recommendationsList = new ArrayList<>();
        List<String> validMediaTypes = getValidMediaTypes(token);

        if (validMediaTypes != null) {
            if (!validMediaTypes.contains(mediaType.toLowerCase())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid media type [" + mediaType + "]. Valid media types are: " + validMediaTypes);
            }
        }

        recommendationsList.addAll(getMediaByPreferredGenres(mediaType, userId, preferredGenres, 10, token));
        Collections.shuffle(recommendationsList);

        return recommendationsList;
    }

    public List<RecommendationDTO> getMediaByPreferredGenres(String mediaType, Long userId, List<String> preferredGenres, int amount, String token) {
        try {
            int preferredAmount = (int) Math.ceil(amount * 0.8);

            List<MediaReferenceDTO> userHistory = getUserMediaHistory(userId, token);
            List<RecommendationDTO> recommendedMediaList = new ArrayList<>();
            List<UserFeedbackDTO> userDislikesList = getUserDislikesById(userId, token);
            List<Long> dislikedMediaIdsList = new ArrayList<>();

            for (int i = 0; i < userDislikesList.size(); i++) {
                dislikedMediaIdsList.add(userDislikesList.get(i).getMediaId());
            }

            List<String> shuffledGenreList = new ArrayList<>(preferredGenres);
            Collections.shuffle(shuffledGenreList);

            for (String genre : shuffledGenreList) {
                if (recommendedMediaList.size() > preferredAmount) {
                    break;
                }

                List<RecommendationDTO> recommendedMedia = mediaServiceClient.get()
                        .uri("/edufy/api/mediaplayer/getmedia/" + mediaType + "/" + genre.toLowerCase())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .retrieve()
                        .body(new ParameterizedTypeReference<List<RecommendationDTO>>() {});

                if (recommendedMedia != null) {
                    for (RecommendationDTO media : recommendedMedia) {
                        if (recommendedMediaList.size() > preferredAmount) {
                            break;
                        }
                        if (!excludeMedia(media.getId(), userHistory, dislikedMediaIdsList) && !isMediaAdded(media.getId(), recommendedMediaList)) {
                            recommendedMediaList.add(media);
                        }
                    }
                }

            }

            List<RecommendationDTO> allMedia = mediaServiceClient.get()
                    .uri("/edufy/api/mediaplayer/getmedia/all/" + mediaType)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<RecommendationDTO>>() {});

            if (allMedia != null) {
                for (RecommendationDTO media : allMedia) {
                    if (recommendedMediaList.size() > amount) {
                        break;
                    }

                    if (!excludeMedia(media.getId(), userHistory, dislikedMediaIdsList) && !isMediaAdded(media.getId(), recommendedMediaList)) {
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

    public List<String> getMediaGenresFromHistory(
            List<MediaReferenceDTO> userHistory,
            List<UserFeedbackDTO> userLikes,
            List<UserFeedbackDTO> userDislikes,
            String token
    ) {
        Map<String, Integer> frequencyMap = new HashMap<>();

        if (userHistory == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User not found.");
        }

        for (MediaReferenceDTO media : userHistory) {
            String genre = getMediaGenre(media.getMediaType(), media.getMediaId(), token);
            int recommendWeight = 1;

            if (isMediaLiked(media.getMediaId(), media.getMediaType(), userLikes)) {
                recommendWeight = 3;
            } else if (isMediaDisliked(media.getMediaId(), media.getMediaType(), userDislikes)) {
                recommendWeight = -3;
            }

            if (recommendWeight > 0 && genre != null) {
                frequencyMap.put(genre, frequencyMap.getOrDefault(genre, 0) + recommendWeight);
            }
        }

        return frequencyMap.entrySet().stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .map(e -> e.getKey())
                .toList();
    }

    public String getMediaGenre(String mediaType, Long mediaId, String token) {
        try {
            return mediaServiceClient.get()
                    .uri("/edufy/api/mediaplayer/getgenre/" + mediaType + "/" + mediaId)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .retrieve()
                    .body(String.class);

        } catch (Exception e) {
            return null;
        }
    }

    public List<String> getValidMediaTypes(String token) {
        try {
            return mediaServiceClient.get()
                    .uri("/edufy/api/mediaplayer/valid-mediatypes")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<String>>() {});

        } catch (Exception e) {
            return null;
        }
    }

    public List<UserFeedbackDTO> getUserLikesById(Long userId, String token) {
        try {
            return  userServiceClient.get()
                    .uri("/edufy/api/users/user/" + userId + "/feedback/likes")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<UserFeedbackDTO>>() {});

        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    public List<UserFeedbackDTO> getUserDislikesById(Long userId, String token) {
        try {
            return  userServiceClient.get()
                    .uri("/edufy/api/users/user/" + userId + "/feedback/dislikes")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<UserFeedbackDTO>>() {});

        } catch (Exception e) {
            return Collections.emptyList();
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

    private boolean isMediaLiked(Long mediaId, String mediaType, List<UserFeedbackDTO> userLikes) {
        for (UserFeedbackDTO likedMedia : userLikes) {
            if (likedMedia.getMediaId().equals(mediaId) && likedMedia.getMediaType().equals(mediaType)) {
                return true;
            }
        }
        return false;
    }

    private boolean isMediaDisliked(Long mediaId, String mediaType, List<UserFeedbackDTO> userDislikes) {
        for (UserFeedbackDTO dislike : userDislikes) {
            if (dislike.getMediaId().equals(mediaId) && dislike.getMediaType().equals(mediaType)) {
                return true;
            }
        }
        return false;
    }

    private boolean excludeMedia(Long mediaId, List<MediaReferenceDTO> userHistory, List<Long> dislikedMediaIds) {
        return isMediaPlayed(mediaId, userHistory) || dislikedMediaIds.contains(mediaId);
    }

}
