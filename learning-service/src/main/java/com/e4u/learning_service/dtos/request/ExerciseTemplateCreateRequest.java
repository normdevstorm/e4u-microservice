package com.e4u.learning_service.dtos.request;

import com.e4u.learning_service.entities.ExerciseTemplate.ExerciseType;
import com.e4u.learning_service.entities.pojos.ExerciseData;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Request model for creating a new ExerciseTemplate.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExerciseTemplateCreateRequest {

    private UUID lessonTemplateId;

    private UUID wordContextTemplateId;

    @NotNull(message = "Exercise type is required")
    private ExerciseType exerciseType;

    /**
     * Exercise data containing the exercise content and correct answer.
     * Supported types: CONTEXTUAL_DISCOVERY, MULTIPLE_CHOICE, MECHANIC_DRILL,
     * TARGET_WORD_INTEGRATION, SENTENCE_BUILDING, ASSISTED_COMPOSITION, CLOZE_WITH_AUDIO
     */
    @NotNull(message = "Exercise data is required")
    private ExerciseData exerciseData;

    /**
     * If set, creates a user-specific exercise.
     * If null, creates a shared template.
     */
    private UUID createdForUserId;
}
