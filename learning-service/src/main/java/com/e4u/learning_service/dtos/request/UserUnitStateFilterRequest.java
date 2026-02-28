package com.e4u.learning_service.dtos.request;

import com.e4u.learning_service.entities.UserUnitState.UnitStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Request model for filtering UserUnitStates with various criteria.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserUnitStateFilterRequest {

    /**
     * Filter by user ID (required for most queries)
     */
    private UUID userId;

    /**
     * Filter by curriculum ID
     */
    private UUID curriculumId;

    /**
     * Filter by unit status (NOT_STARTED, IN_PROGRESS, COMPLETED)
     */
    private UnitStatus status;

    /**
     * Filter units with fast-tracked flag
     */
    private Boolean isFastTracked;

    /**
     * Minimum proficiency score filter
     */
    private Float minProficiencyScore;

    /**
     * Maximum proficiency score filter
     */
    private Float maxProficiencyScore;

    /**
     * Minimum priority score filter
     */
    private Integer minPriorityScore;

    /**
     * Maximum priority score filter
     */
    private Integer maxPriorityScore;

    // Pagination
    @Builder.Default
    private Integer page = 0;

    @Builder.Default
    private Integer size = 20;

    // Sorting
    @Builder.Default
    private String sortBy = "defaultOrder";

    @Builder.Default
    private String sortDirection = "ASC";
}
