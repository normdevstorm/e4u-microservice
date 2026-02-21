package com.e4u.lesson_service.models.request;

import com.e4u.lesson_service.entities.DynamicLesson.LessonStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Request model for filtering DynamicLessons.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DynamicLessonFilterRequest {

    private UUID userId;

    private UUID userUnitStateId;

    private LessonStatus status;

    private Float minAccuracyRate;

    private Float maxAccuracyRate;

    private LocalDateTime startedAfter;

    private LocalDateTime startedBefore;

    private LocalDateTime completedAfter;

    private LocalDateTime completedBefore;

    // Pagination
    @Builder.Default
    private Integer page = 0;

    @Builder.Default
    private Integer size = 20;

    // Sorting
    @Builder.Default
    private String sortBy = "createdAt";

    @Builder.Default
    private String sortDirection = "DESC";
}
