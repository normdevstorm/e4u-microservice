package com.e4u.learning_service.dtos.response;

import com.e4u.learning_service.entities.ExerciseTemplate.ExerciseType;
import com.e4u.learning_service.entities.pojos.ExerciseData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

/**
 * Response model for ExerciseTemplate.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExerciseTemplateResponse {

    private UUID id;

    private UUID lessonTemplateId;

    private UUID wordContextTemplateId;

    private String wordLemma;

    private ExerciseType exerciseType;

    /**
     * Exercise data containing the exercise content.
     * Note: correctAnswer is embedded in the data - filter it out for client responses if needed.
     */
    private ExerciseData exerciseData;

    private UUID createdForUserId;

    private Instant createdAt;

    private Instant updatedAt;
}
