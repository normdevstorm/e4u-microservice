package com.e4u.lesson_service.models.request;

import com.e4u.lesson_service.entities.LessonExercise.ExerciseType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Request model for filtering LessonExercises.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LessonExerciseFilterRequest {

    private UUID lessonId;

    private UUID wordInstanceId;

    private UUID unitId;

    private ExerciseType exerciseType;

    private Boolean isCompleted;

    private Boolean isCorrect;

    // Pagination
    @Builder.Default
    private Integer page = 0;

    @Builder.Default
    private Integer size = 20;

    // Sorting
    @Builder.Default
    private String sortBy = "sequenceOrder";

    @Builder.Default
    private String sortDirection = "ASC";
}
