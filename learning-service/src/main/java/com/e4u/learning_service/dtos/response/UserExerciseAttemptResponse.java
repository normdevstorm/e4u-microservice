package com.e4u.learning_service.dtos.response;

import com.e4u.learning_service.entities.ExerciseTemplate.ExerciseType;
import com.e4u.learning_service.entities.pojos.ExerciseData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/**
 * Response model for UserExerciseAttempt.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserExerciseAttemptResponse {

    private UUID id;

    private UUID sessionId;

    private UUID exerciseTemplateId;

    private ExerciseType exerciseType;

    private Map<String, Object> userAnswer;

    private Boolean isCorrect;

    private Integer score;

    private Integer timeTakenSeconds;

    /**
     * The exercise data with correct answer embedded (shown after submission)
     */
    private ExerciseData exerciseData;

    /**
     * Optional feedback message
     */
    private String feedback;

    private Instant createdAt;
}
