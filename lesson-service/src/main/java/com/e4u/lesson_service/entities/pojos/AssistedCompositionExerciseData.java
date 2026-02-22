package com.e4u.lesson_service.entities.pojos;

import com.e4u.lesson_service.common.constants.Constant;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

/**
 * Assisted Composition Exercise Data (formerly Partial Output)
 * 
 * This exercise type provides the sentence structure (the "skeleton"),
 * and the user only has to fill in the "muscle" (the target vocabulary).
 * 
 * Example:
 * - AI Context: "Complete the sentence using the word Sustainable."
 * - Prompt: "To remain competitive, the factory adopted a _____________
 * strategy to reduce waste."
 * - User Action: User types/speaks only the missing word or short phrase:
 * "sustainable" or "more sustainable"
 * - AI Feedback: "Correct! You could also say 'environmentally sustainable'."
 * 
 * Why it works: It focuses 100% of the user's brainpower on the target word's
 * fit in the context, rather than worrying about the rest of the grammar.
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class AssistedCompositionExerciseData extends ExerciseData {

    /**
     * The instruction/context for the user (e.g., "Complete the sentence using the
     * word Sustainable")
     */
    private String prompt;

    /**
     * The sentence with blank(s) for the user to fill in
     * (e.g., "To remain competitive, the factory adopted a _____________ strategy
     * to reduce waste.")
     */
    private String setupText;

    /**
     * The expected word or phrase the user should provide
     */
    private String expectedWord;

    /**
     * Minimum number of words in the user's response
     */
    private Integer minWordCount;

    /**
     * A hint to assist learners in completing the exercise
     */
    private String hint;

    /**
     * Alternative correct answers (e.g., "more sustainable", "environmentally
     * sustainable")
     */
    private java.util.List<String> alternativeAnswers;

    /**
     * Feedback to show when the user answers correctly
     */
    private String correctFeedback;

    @Override
    public String getType() {
        return Constant.ASSISTED_COMPOSITION_IDENTIFIER;
    }
}
