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
 * Response DTO for Curriculum with full details including units.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CurriculumDetailResponse {

    private UUID id;
    private String curriculumName;
    private UUID goalId;
    private String goalName;
    private String targetGoals;
    private String description;
    private Boolean isActive;
    private List<CurriculumUnitResponse> units;
    private Instant createdAt;
    private Instant updatedAt;
}
