package com.e4u.learning_service.dtos.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.UUID;

/**
 * Request model for submitting an exercise attempt.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserExerciseAttemptRequest {

    @NotNull(message = "Session ID is required")
    private UUID sessionId;

    @NotNull(message = "Exercise template ID is required")
    private UUID exerciseTemplateId;

    @NotNull(message = "User answer is required")
    private Map<String, Object> userAnswer;

    /**
     * Time taken to complete this exercise (in seconds)
     */
    private Integer timeTakenSeconds;
}
