package com.e4u.learning_service.dtos.response;

import com.e4u.learning_service.entities.UserLessonSession.SessionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Detailed response model for UserLessonSession including exercises.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserLessonSessionDetailResponse {

    private UUID id;

    private UUID userId;

    private UUID lessonTemplateId;

    private String lessonName;

    private UUID userUnitStateId;

    private SessionStatus status;

    private Integer totalItems;

    private Integer completedItems;

    private Integer correctItems;

    private Float accuracyRate;

    /**
     * Exercises for this session (without correct answers exposed)
     */
    private List<ExerciseTemplateResponse> exercises;

    /**
     * User's attempts in this session
     */
    private List<UserExerciseAttemptResponse> attempts;

    /**
     * Index of the current exercise (for resuming)
     */
    private Integer currentExerciseIndex;

    private Instant createdAt;

    private Instant updatedAt;
}
