package com.e4u.lesson_service.models.response;

import com.e4u.lesson_service.entities.UserUnitState.UnitStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Response DTO that combines CurriculumUnit information with UserUnitState.
 * Provides a unified view of unit details along with user's learning progress.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserUnitStateResponse {

    // ===================== Unit Information (from curriculum-service)
    // =====================

    /**
     * Unique identifier of the unit
     */
    private UUID unitId;

    /**
     * ID of the curriculum this unit belongs to
     */
    private UUID curriculumId;

    /**
     * Name of the curriculum
     */
    private String curriculumName;

    /**
     * Name of the unit
     */
    private String unitName;

    /**
     * Required proficiency level for this unit
     */
    private String requiredProficiencyLevel;

    /**
     * Default display order within the curriculum
     */
    private Integer defaultOrder;

    /**
     * Keywords associated with this unit
     */
    private List<String> baseKeywords;

    /**
     * Description of the unit
     */
    private String description;

    /**
     * Whether this unit is currently active
     */
    private Boolean isActive;

    /**
     * Number of words in this unit
     */
    private Long wordCount;

    // ===================== User State Information (from lesson-service)
    // =====================

    /**
     * Unique identifier of the user's unit state record
     */
    private UUID stateId;

    /**
     * ID of the user
     */
    private UUID userId;

    /**
     * Current status of the unit for this user
     */
    private UnitStatus status;

    /**
     * Current priority score for scheduling
     */
    private Integer currentPriorityScore;

    /**
     * Whether this unit is fast-tracked for the user
     */
    private Boolean isFastTracked;

    /**
     * User's proficiency score for this unit
     */
    private Float proficiencyScore;

    /**
     * Difficulty modifier based on user's performance
     */
    private Float difficultyModifier;

    /**
     * Total number of lessons in this unit for the user
     */
    private Integer lessonCount;

    /**
     * Last time the user interacted with this unit
     */
    private LocalDateTime lastInteractionAt;

    /**
     * When the user started learning this unit
     */
    private Instant stateCreatedAt;

    /**
     * Last update time of the state
     */
    private Instant stateUpdatedAt;
}
