package com.e4u.learning_service.dtos.request;

import com.e4u.learning_service.entities.ExerciseTemplate.ExerciseType;
import com.e4u.learning_service.entities.pojos.ExerciseData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request model for updating an ExerciseTemplate.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExerciseTemplateUpdateRequest {

    private ExerciseType exerciseType;

    /**
     * Exercise data containing the exercise content and correct answer.
     * Supported types: CONTEXTUAL_DISCOVERY, MULTIPLE_CHOICE, MECHANIC_DRILL,
     * TARGET_WORD_INTEGRATION, SENTENCE_BUILDING, ASSISTED_COMPOSITION, CLOZE_WITH_AUDIO
     */
    private ExerciseData exerciseData;
}
