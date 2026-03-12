package com.e4u.learning_service.services;

import com.e4u.learning_service.dtos.request.BaselineEvaluateRequest;
import com.e4u.learning_service.dtos.response.BaselineEvaluateResponse;
import com.e4u.learning_service.dtos.response.BaselineQuestionResponse;

import java.util.List;

/**
 * Provides the F-02 baseline English proficiency placement test.
 */
public interface BaselineAssessmentService {

    /**
     * Returns all placement questions (correctAnswer omitted).
     * Questions are ordered by CEFR tier (A1 → C1) then sort_order.
     */
    List<BaselineQuestionResponse> getQuestions();

    /**
     * Evaluates submitted answers and returns the derived CEFR level + score.
     *
     * @param request map of questionId → selectedOption
     * @return evaluation result with cefrLevel, score, counts
     */
    BaselineEvaluateResponse evaluate(BaselineEvaluateRequest request);
}
