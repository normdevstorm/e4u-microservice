package com.e4u.lesson_service.models.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for submitting an exercise answer.
 * 
 * The answer field accepts different types based on exercise type:
 * - CONTEXTUAL_DISCOVERY: "ACKNOWLEDGED" (string)
 * - MULTIPLE_CHOICE: 0, 1, 2, 3 (option index as string or number)
 * - MECHANIC_DRILL: "word" (string)
 * - ASSISTED_COMPOSITION: "word or phrase" (string)
 * - SENTENCE_BUILDING: ["word1", "word2", ...] (JSON array as string)
 * - TARGET_WORD_INTEGRATION: "full sentence" (string)
 * - CLOZE_WITH_AUDIO: "word" (string)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Request payload for submitting an exercise answer")
public class ExerciseSubmitRequest {

    @NotNull(message = "Answer is required")
    @Schema(description = "The user's answer. Format varies by exercise type.", example = "sustainable", requiredMode = Schema.RequiredMode.REQUIRED)
    private Object answer;

    @Schema(description = "Time spent on this exercise in seconds", example = "45")
    @Builder.Default
    private Integer timeSpentSeconds = 0;

    @Schema(description = "Input method used by the user", example = "TYPED", allowableValues = { "TYPED", "SPOKEN",
            "SELECTED" })
    @Builder.Default
    private InputMethod inputMethod = InputMethod.TYPED;

    public enum InputMethod {
        TYPED,
        SPOKEN,
        SELECTED
    }
}
