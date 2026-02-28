package com.e4u.learning_service.dtos.request;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Request DTO for partially updating a CurriculumUnit.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CurriculumUnitUpdateRequest {

    @Size(max = 100, message = "Unit name must not exceed 100 characters")
    private String unitName;

    @Size(max = 5, message = "Proficiency level must not exceed 5 characters")
    private String requiredProficiencyLevel;

    private Integer defaultOrder;

    private List<String> baseKeywords;

    private String description;

    private Boolean isActive;
}
