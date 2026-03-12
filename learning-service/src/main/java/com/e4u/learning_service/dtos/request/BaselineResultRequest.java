package com.e4u.learning_service.dtos.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Request DTO for F-02 baseline step.
 * Persists the CEFR level evaluated by POST /v1/baseline/evaluate.
 */
@Data
public class BaselineResultRequest {

    @NotBlank(message = "CEFR level must not be blank")
    private String cefrLevel;
}
