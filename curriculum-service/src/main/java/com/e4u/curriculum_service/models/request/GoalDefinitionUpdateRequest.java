package com.e4u.curriculum_service.models.request;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Request DTO for partially updating a GoalDefinition.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoalDefinitionUpdateRequest {

    @Size(max = 50, message = "Goal name must not exceed 50 characters")
    private String goalName;

    private List<String> skillsFocus;

    private Boolean isActive;
}
