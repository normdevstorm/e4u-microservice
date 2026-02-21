package com.e4u.curriculum_service.models.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Response DTO for GoalDefinition.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GoalDefinitionResponse {

    private UUID id;
    private String goalName;
    private List<String> skillsFocus;
    private Boolean isActive;
    private Instant createdAt;
    private Instant updatedAt;
}
