package com.e4u.curriculum_service.client;

import com.e4u.curriculum_service.client.dto.LessonServiceResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Fallback implementation for LessonServiceClient.
 * 
 * Provides default responses when lesson-service is unavailable.
 */
@Slf4j
@Component
public class LessonServiceClientFallback implements LessonServiceClient {

    @Override
    public LessonServiceResponse<List<UUID>> getVocabInstancesByUserAndUnit(UUID userId, UUID unitId) {
        log.warn(
                "Fallback: Unable to fetch vocab instances for user {} and unit {}. Lesson service may be unavailable.",
                userId, unitId);
        return LessonServiceResponse.<List<UUID>>builder()
                .success(false)
                .data(Collections.emptyList())
                .message("Lesson service unavailable")
                .build();
    }

    @Override
    public LessonServiceResponse<List<UUID>> createVocabInstancesFromWords(UUID userId, List<UUID> wordIds) {
        log.warn("Fallback: Unable to create vocab instances for user {}. Lesson service may be unavailable.", userId);
        return LessonServiceResponse.<List<UUID>>builder()
                .success(false)
                .data(Collections.emptyList())
                .message("Lesson service unavailable")
                .build();
    }
}
