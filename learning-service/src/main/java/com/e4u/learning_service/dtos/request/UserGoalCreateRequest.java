package com.e4u.learning_service.dtos.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Request DTO for creating a UserGoal.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserGoalCreateRequest {

    @NotNull(message = "User ID is required")
    private UUID userId;

    @NotNull(message = "Goal ID is required")
    private UUID goalId;

    @Builder.Default
    private Boolean isPrimary = false;
}
