package com.e4u.learning_service.dtos.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Request body for POST /v1/baseline/evaluate.
 * Maps questionId (UUID string) → selected option string.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaselineEvaluateRequest {

    /**
     * Key: question UUID as string.
     * Value: the option string the user selected.
     * Must contain at least one entry.
     */
    @NotNull(message = "answers must not be null")
    private Map<String, String> answers;
}
