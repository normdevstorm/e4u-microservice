package com.e4u.curriculum_service.models.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

/**
 * Request DTO for creating a CurriculumUnit.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CurriculumUnitCreateRequest {

    @NotNull(message = "Curriculum ID is required")
    private UUID curriculumId;

    @NotBlank(message = "Unit name is required")
    @Size(max = 100, message = "Unit name must not exceed 100 characters")
    private String unitName;

    @Size(max = 5, message = "Proficiency level must not exceed 5 characters")
    private String requiredProficiencyLevel;

    private Integer defaultOrder;

    private List<String> baseKeywords;

    private String description;

    @Builder.Default
    private Boolean isActive = true;
}
