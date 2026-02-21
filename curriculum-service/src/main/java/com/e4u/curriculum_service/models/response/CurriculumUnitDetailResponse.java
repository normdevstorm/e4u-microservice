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
 * Response DTO for CurriculumUnit with full details including base words.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CurriculumUnitDetailResponse {

    private UUID id;
    private UUID curriculumId;
    private String curriculumName;
    private String unitName;
    private String requiredProficiencyLevel;
    private Integer defaultOrder;
    private List<String> baseKeywords;
    private String description;
    private Boolean isActive;
    private List<GlobalDictionaryResponse> baseWords;
    private Instant createdAt;
    private Instant updatedAt;
}
