package com.e4u.learning_service.dtos.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Request model for starting or resuming a lesson session.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserLessonSessionStartRequest {

    private UUID userId;

    @NotNull(message = "Lesson template ID is required")
    private UUID lessonTemplateId;

    /**
     * Optional: Reference to user's unit state
     */
    private UUID userUnitStateId;
}
