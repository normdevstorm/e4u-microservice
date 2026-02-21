package com.e4u.lesson_service.models.response;

import com.e4u.lesson_service.entities.DynamicLesson.LessonStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Detailed response model for DynamicLesson including full vocab items.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DynamicLessonDetailResponse {

    private UUID id;

    private UUID userId;

    private UUID userUnitStateId;

    private LessonStatus status;

    private Float accuracyRate;

    private LocalDateTime startedAt;

    private LocalDateTime completedAt;

    private Integer totalItems;

    private Integer completedItems;

    private List<UserVocabInstanceResponse> vocabItems;

    private Instant createdAt;

    private Instant updatedAt;
}
