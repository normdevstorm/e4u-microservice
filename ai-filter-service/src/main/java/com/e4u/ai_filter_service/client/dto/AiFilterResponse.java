package com.e4u.ai_filter_service.client.dto;

import com.e4u.ai_filter_service.domain.enums.WordRelevanceTier;

import java.util.UUID;

/**
 * Response received from the AI relevance-scoring API for a single user-word
 * pair.
 *
 * @param wordId         UUID of the processed word (echoed from the request)
 * @param userId         UUID of the learner (echoed from the request)
 * @param relevanceTier  AI-assigned relevance tier: HIGH, MEDIUM, or LOW
 * @param relevanceScore Continuous relevance score (0.0 – 1.0); null if not
 *                       provided
 * @param reason         Short explanation from the AI for the assigned tier
 */
public record AiFilterResponse(
        UUID wordId,
        UUID userId,
        WordRelevanceTier relevanceTier,
        Float relevanceScore,
        String reason) {
}
