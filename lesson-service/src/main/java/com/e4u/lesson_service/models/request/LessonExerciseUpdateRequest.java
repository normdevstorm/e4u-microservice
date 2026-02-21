package com.e4u.lesson_service.models.request;

import com.e4u.lesson_service.entities.LessonExercise.ExerciseType;
import com.e4u.lesson_service.entities.pojos.ExerciseData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Request model for partially updating a LessonExercise.
 * All fields are optional - only provided fields will be updated.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LessonExerciseUpdateRequest {

    private UUID lessonId;

    private UUID wordInstanceId;

    private ExerciseType exerciseType;

    private ExerciseData exerciseData;

    private Integer sequenceOrder;

    private Boolean isCompleted;

    private Boolean isCorrect;

    private String userAnswer;

    private Integer timeSpentSeconds;
}
