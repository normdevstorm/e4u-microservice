package com.e4u.curriculum_service.models.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for partially updating a UserGoal.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserGoalUpdateRequest {

    private Boolean isPrimary;
}
