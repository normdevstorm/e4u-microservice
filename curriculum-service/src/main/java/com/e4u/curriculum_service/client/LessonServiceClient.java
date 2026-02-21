package com.e4u.curriculum_service.client;

import com.e4u.curriculum_service.client.dto.LessonServiceResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.UUID;

/**
 * Feign client for communicating with the Lesson Service.
 * 
 * <p>
 * This client provides methods to interact with the lesson-service
 * microservice for lesson generation and management.
 * </p>
 */
@FeignClient(name = "lesson-service", url = "${client.lesson-service.url:http://localhost:8082}", fallback = LessonServiceClientFallback.class)
public interface LessonServiceClient {

        /**
         * Get user's vocabulary instances for a specific unit.
         * 
         * @param userId The UUID of the user
         * @param unitId The unit ID
         * @return List of vocabulary instance IDs
         */
        @GetMapping("/v1/vocab-instances/user/{userId}/unit/{unitId}")
        LessonServiceResponse<List<UUID>> getVocabInstancesByUserAndUnit(
                        @PathVariable("userId") UUID userId,
                        @PathVariable("unitId") UUID unitId);

        /**
         * Create vocabulary instances for user from unit base words.
         * 
         * @param userId  The UUID of the user
         * @param wordIds The list of word IDs from global dictionary
         * @return Response with created vocabulary instance IDs
         */
        @PostMapping("/v1/vocab-instances/user/{userId}/from-words")
        LessonServiceResponse<List<UUID>> createVocabInstancesFromWords(
                        @PathVariable("userId") UUID userId,
                        @RequestBody List<UUID> wordIds);
}
