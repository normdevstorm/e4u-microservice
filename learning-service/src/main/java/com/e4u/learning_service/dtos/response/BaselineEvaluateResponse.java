package com.e4u.learning_service.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Result of the baseline placement assessment.
 *
 * <p>
 * Scoring algorithm (see BaselineAssessmentServiceImpl):
 * <ul>
 * <li>Each question is worth points proportional to its CEFR tier weight.</li>
 * <li>The {@code score} is a 0–100 integer percentage of weighted correct
 * answers.</li>
 * <li>The {@code cefrLevel} is derived from the score bucket.</li>
 * </ul>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BaselineEvaluateResponse {

    /** Derived CEFR level: A1 | A2 | B1 | B2 | C1 */
    private String cefrLevel;

    /** Weighted percentage score, 0–100 */
    private int score;

    /** Number of correct answers out of total questions attempted */
    private int correctCount;

    /** Total number of questions in the test */
    private int totalCount;
}
