package com.e4u.curriculum_service.models.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Request DTO for filtering CurriculumUnits.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CurriculumUnitFilterRequest {

    private UUID curriculumId;
    private String unitName;
    private String requiredProficiencyLevel;
    private Boolean isActive;
    private String keywordContains;

    // Pagination
    @Builder.Default
    private Integer page = 0;
    @Builder.Default
    private Integer size = 20;
    @Builder.Default
    private String sortBy = "defaultOrder";
    @Builder.Default
    private String sortDirection = "ASC";
}
