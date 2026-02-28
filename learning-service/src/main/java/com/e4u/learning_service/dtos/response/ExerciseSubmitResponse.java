package com.e4u.learning_service.dtos.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

/**
 * Response DTO for exercise submission result.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Response payload after submitting an exercise answer")
public class ExerciseSubmitResponse {

    @Schema(description = "Exercise ID")
    private UUID exerciseId;

    @Schema(description = "Whether the answer is correct")
    private Boolean isCorrect;

    @Schema(description = "Score earned for this submission (0-100)")
    private Integer score;

    @Schema(description = "Current attempt number")
    private Integer attemptNumber;

    @Schema(description = "Maximum attempts allowed")
    private Integer maxAttempts;

    @Schema(description = "Feedback details")
    private ExerciseFeedback feedback;

    @Schema(description = "Current exercise state after submission")
    private ExerciseState exerciseState;

    @Schema(description = "Overall lesson progress")
    private LessonProgress lessonProgress;

    @Schema(description = "Vocabulary word progress")
    private VocabProgress vocabProgress;

    /**
     * Feedback provided to the user after submission
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ExerciseFeedback {

        @Schema(description = "Main feedback message", example = "Great job! That's correct!")
        private String message;

        @Schema(description = "The correct answer (only shown after max attempts)", example = "sustainable")
        private String correctAnswer;

        @Schema(description = "User's submitted answer", example = "sustainabel")
        private String userAnswer;

        @Schema(description = "Hint for next attempt", example = "Think about the environment")
        private String hint;

        @Schema(description = "Detailed explanation of the correct answer")
        private String explanation;

        @Schema(description = "Alternative acceptable answers")
        private List<String> alternatives;

        @Schema(description = "Grammar or usage notes from AI evaluation")
        private String grammarNotes;
    }

    /**
     * Current state of the exercise
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ExerciseState {

        @Schema(description = "Whether the exercise is marked as completed")
        private Boolean isCompleted;

        @Schema(description = "Whether the user can retry this exercise")
        private Boolean canRetry;

        @Schema(description = "Number of attempts remaining")
        private Integer attemptsRemaining;
    }

    /**
     * Progress within the current lesson
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LessonProgress {

        @Schema(description = "Lesson ID")
        private UUID lessonId;

        @Schema(description = "Number of completed exercises")
        private Integer completedItems;

        @Schema(description = "Total number of exercises in the lesson")
        private Integer totalItems;

        @Schema(description = "Number of correctly answered exercises")
        private Integer correctItems;

        @Schema(description = "Current accuracy rate (0.0 - 1.0)")
        private Float accuracyRate;
    }

    /**
     * Progress for the vocabulary word being practiced
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VocabProgress {

        @Schema(description = "Vocabulary instance ID")
        private UUID wordId;

        @Schema(description = "The word being practiced")
        private String wordText;

        @Schema(description = "Current proficiency score (0.0 - 1.0)")
        private Float proficiencyScore;

        @Schema(description = "Whether the word is currently being learned")
        private Boolean isLearning;

        @Schema(description = "Whether the word is mastered")
        private Boolean isMastered;
    }
}
