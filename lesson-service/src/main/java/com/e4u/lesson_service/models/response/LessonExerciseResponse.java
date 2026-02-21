package com.e4u.lesson_service.models.response;

import com.e4u.lesson_service.entities.LessonExercise.ExerciseType;
import com.e4u.lesson_service.entities.pojos.ExerciseData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

/**
 * Response model for LessonExercise.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LessonExerciseResponse {

    private UUID id;

    private UUID lessonId;

    private UUID wordInstanceId;

    private ExerciseType exerciseType;

    private ExerciseData exerciseData;

    private Integer sequenceOrder;

    private Boolean isCompleted;

    private Boolean isCorrect;

    private String userAnswer;

    private Integer timeSpentSeconds;

    private Instant createdAt;

    private Instant updatedAt;
}
