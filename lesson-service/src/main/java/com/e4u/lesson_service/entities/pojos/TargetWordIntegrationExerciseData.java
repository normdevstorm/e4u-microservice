package com.e4u.lesson_service.entities.pojos;

import com.e4u.lesson_service.common.constants.Constant;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Target-Word Integration Exercise Data
 * 
 * This exercise type provides context illustrated by hints (words)
 * and a simpler version of the sentence. Users form a sentence using
 * the target word, and the AI evaluates and provides feedback/fixes.
 * 
 * Example:
 * - Context hints: ["web", "development", "popular"]
 * - Simplified sentence: "A ___ helps developers build applications faster."
 * - Target word: "framework"
 * - User forms: "Spring Boot is a popular Java framework for web development."
 * - AI evaluates and provides feedback
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class TargetWordIntegrationExerciseData extends ExerciseData {

    /**
     * The prompt/instruction for the user
     */
    private String prompt;

    /**
     * The target word the user must integrate into their sentence
     */
    private String targetWord;

    /**
     * Context hints - words that help illustrate the context
     */
    private List<String> contextHints;

    /**
     * A simpler version of the sentence to guide the user
     */
    private String simplifiedSentence;

    /**
     * Minimum number of words required in the response
     */
    private Integer minWords;

    /**
     * Maximum number of words allowed in the response
     */
    private Integer maxWords;

    /**
     * An example response to guide the user
     */
    private String exampleResponse;

    @Override
    public String getType() {
        return Constant.TARGET_WORD_INTEGRATION_IDENTIFIER;
    }
}
