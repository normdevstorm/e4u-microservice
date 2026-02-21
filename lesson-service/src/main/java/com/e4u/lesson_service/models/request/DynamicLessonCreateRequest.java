package com.e4u.lesson_service.models.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

/**
 * Request model for creating a new DynamicLesson with vocab instances.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DynamicLessonCreateRequest {

    @NotNull(message = "User ID is required")
    private UUID userId;

    /**
     * Optional: Reference to the UserUnitState this lesson belongs to
     */
    private UUID userUnitStateId;

    /**
     * List of UserVocabInstance IDs to include in this lesson
     */
    @NotEmpty(message = "At least one vocabulary item is required")
    private List<UUID> vocabInstanceIds;
}
