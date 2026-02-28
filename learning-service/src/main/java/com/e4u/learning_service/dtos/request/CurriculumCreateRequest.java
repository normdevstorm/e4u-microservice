package com.e4u.learning_service.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Request DTO for creating a Curriculum.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CurriculumCreateRequest {

    @NotBlank(message = "Curriculum name is required")
    @Size(max = 100, message = "Curriculum name must not exceed 100 characters")
    private String curriculumName;

    private UUID goalId;

    private String targetGoals;

    private String description;

    @Builder.Default
    private Boolean isActive = true;
}
