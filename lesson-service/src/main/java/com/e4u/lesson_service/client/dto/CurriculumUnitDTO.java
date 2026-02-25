package com.e4u.lesson_service.client.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * DTO representing a curriculum unit from the curriculum-service.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CurriculumUnitDTO {

    private UUID id;
    private UUID curriculumId;
    private String curriculumName;
    private String unitName;
    private String requiredProficiencyLevel;
    private Integer defaultOrder;
    private List<String> baseKeywords;
    private String description;
    private Boolean isActive;
    private Long wordCount;
    private Instant createdAt;
    private Instant updatedAt;
}
