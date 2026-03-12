package com.e4u.learning_service.dtos.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

import java.util.List;

/**
 * Request DTO for F-01 profiling step.
 * Updates occupation, interests, and daily time commitment.
 */
@Data
public class UserProfilingRequest {

    private String occupation;

    private List<String> interests;

    @Min(value = 5, message = "Daily time commitment must be at least 5 minutes")
    @Max(value = 480, message = "Daily time commitment must be at most 480 minutes")
    private Integer dailyTimeCommitment;
}
