package com.e4u.curriculum_service.models.request;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Request DTO for partially updating a Curriculum.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CurriculumUpdateRequest {

    @Size(max = 100, message = "Curriculum name must not exceed 100 characters")
    private String curriculumName;

    private UUID goalId;

    private String targetGoals;

    private String description;

    private Boolean isActive;
}
