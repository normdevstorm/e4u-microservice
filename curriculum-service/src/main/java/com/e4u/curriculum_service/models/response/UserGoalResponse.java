package com.e4u.curriculum_service.models.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

/**
 * Response DTO for UserGoal.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserGoalResponse {

    private UUID userId;
    private UUID goalId;
    private String goalName;
    private Boolean isPrimary;
    private Instant startedAt;
    private Instant createdAt;
    private Instant updatedAt;

    // Optional: Include full goal details
    private GoalDefinitionResponse goal;
}
