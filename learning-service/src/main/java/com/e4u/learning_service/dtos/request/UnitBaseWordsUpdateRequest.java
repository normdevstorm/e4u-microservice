package com.e4u.learning_service.dtos.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

/**
 * Request DTO for updating base words in a CurriculumUnit.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UnitBaseWordsUpdateRequest {

    @NotNull(message = "Unit ID is required")
    private UUID unitId;

    /**
     * List of word IDs to add to the unit
     */
    private List<UUID> addWordIds;

    /**
     * List of word IDs to remove from the unit
     */
    private List<UUID> removeWordIds;
}
