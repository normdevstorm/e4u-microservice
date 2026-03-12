package com.e4u.learning_service.services.impl;

import com.e4u.learning_service.dtos.request.BaselineEvaluateRequest;
import com.e4u.learning_service.dtos.response.BaselineEvaluateResponse;
import com.e4u.learning_service.dtos.response.BaselineQuestionResponse;
import com.e4u.learning_service.entities.BaselineQuestion;
import com.e4u.learning_service.repositories.BaselineQuestionRepository;
import com.e4u.learning_service.services.BaselineAssessmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BaselineAssessmentServiceImpl implements BaselineAssessmentService {

    private final BaselineQuestionRepository questionRepository;

    // ── Tier weights for weighted scoring ────────────────────────────────────
    private static final Map<String, Integer> TIER_WEIGHT = Map.of(
            "A1", 1,
            "A2", 2,
            "B1", 3,
            "B2", 4,
            "C1", 5);

    @Override
    @Transactional(readOnly = true)
    public List<BaselineQuestionResponse> getQuestions() {
        return questionRepository.findAllByOrderByCefrTierAscSortOrderAsc()
                .stream()
                .map(q -> BaselineQuestionResponse.builder()
                        .id(q.getId().toString())
                        .cefrTier(q.getCefrTier())
                        .prompt(q.getPrompt())
                        .options(q.getOptions())
                        .sortOrder(q.getSortOrder())
                        .build())
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public BaselineEvaluateResponse evaluate(BaselineEvaluateRequest request) {
        // Load all questions and index by id for O(1) lookup
        Map<String, BaselineQuestion> byId = questionRepository.findAll()
                .stream()
                .collect(Collectors.toMap(
                        q -> q.getId().toString(),
                        Function.identity()));

        Map<String, String> answers = request.getAnswers();
        int totalWeight = 0;
        int correctWeight = 0;
        int correctCount = 0;
        int totalCount = answers.size();

        for (Map.Entry<String, String> entry : answers.entrySet()) {
            String questionId = entry.getKey();
            String selectedOpt = entry.getValue();

            BaselineQuestion question = byId.get(questionId);
            if (question == null)
                continue; // skip unknown question IDs gracefully

            int weight = TIER_WEIGHT.getOrDefault(question.getCefrTier(), 1);
            totalWeight += weight;

            if (question.getCorrectAnswer().equalsIgnoreCase(selectedOpt.trim())) {
                correctWeight += weight;
                correctCount++;
            }
        }

        // Weighted percentage 0-100
        int score = (totalWeight == 0) ? 0 : (int) Math.round((correctWeight * 100.0) / totalWeight);

        String cefrLevel = deriveCefrLevel(score);

        return BaselineEvaluateResponse.builder()
                .cefrLevel(cefrLevel)
                .score(score)
                .correctCount(correctCount)
                .totalCount(totalCount)
                .build();
    }

    // ── CEFR derivation from weighted score ──────────────────────────────────

    /**
     * Converts a weighted percentage score (0–100) to a CEFR level.
     *
     * <p>
     * Buckets are generous to avoid under-placing new learners:
     * <ul>
     * <li>≥ 85 → C1</li>
     * <li>≥ 65 → B2</li>
     * <li>≥ 45 → B1</li>
     * <li>≥ 25 → A2</li>
     * <li>&lt; 25 → A1</li>
     * </ul>
     */
    private static String deriveCefrLevel(int score) {
        if (score >= 85)
            return "C1";
        if (score >= 65)
            return "B2";
        if (score >= 45)
            return "B1";
        if (score >= 25)
            return "A2";
        return "A1";
    }
}
