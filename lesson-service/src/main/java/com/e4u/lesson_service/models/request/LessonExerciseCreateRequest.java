package com.e4u.lesson_service.models.request;

import com.e4u.lesson_service.entities.LessonExercise.ExerciseType;
import com.e4u.lesson_service.entities.pojos.ExerciseData;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Request model for creating a new LessonExercise.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LessonExerciseCreateRequest {

    @NotNull(message = "Lesson ID is required")
    private UUID lessonId;

    @NotNull(message = "Word instance ID is required")
    private UUID wordInstanceId;

    @NotNull(message = "Exercise type is required")
    private ExerciseType exerciseType;

    @NotNull(message = "Exercise data is required")
    private ExerciseData exerciseData;

    private Integer sequenceOrder;
}
