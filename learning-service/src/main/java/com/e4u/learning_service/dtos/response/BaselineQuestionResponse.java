package com.e4u.learning_service.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Baseline question DTO sent to the FE.
 * Deliberately omits {@code correctAnswer} to prevent cheating.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BaselineQuestionResponse {

    private String id;

    /** CEFR tier this question targets: A1 | A2 | B1 | B2 | C1 */
    private String cefrTier;

    /** Question stem / fill-in-the-blank sentence */
    private String prompt;

    /** 4-choice options list */
    private List<String> options;

    /** Display ordering hint */
    private Integer sortOrder;
}
