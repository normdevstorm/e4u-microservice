package com.e4u.learning_service.dtos.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Request DTO for filtering Curricula.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CurriculumFilterRequest {

    private String curriculumName;
    private UUID goalId;
    private Boolean isActive;
    private String targetGoalsContains;

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
