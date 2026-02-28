package com.e4u.learning_service.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Request DTO for creating a GoalDefinition.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoalDefinitionCreateRequest {

    @NotBlank(message = "Goal name is required")
    @Size(max = 50, message = "Goal name must not exceed 50 characters")
    private String goalName;

    private List<String> skillsFocus;

    @Builder.Default
    private Boolean isActive = true;
}
