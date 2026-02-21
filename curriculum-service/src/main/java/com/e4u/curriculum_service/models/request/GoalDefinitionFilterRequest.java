package com.e4u.curriculum_service.models.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for filtering GoalDefinitions.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoalDefinitionFilterRequest {

    private String goalName;
    private Boolean isActive;
    private String skillsFocusContains;

    // Pagination
    @Builder.Default
    private Integer page = 0;
    @Builder.Default
    private Integer size = 20;
    @Builder.Default
    private String sortBy = "createdAt";
    @Builder.Default
    private String sortDirection = "DESC";
}
