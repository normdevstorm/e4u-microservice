package com.e4u.ai_filter_service.client;

import com.e4u.ai_filter_service.client.dto.AiFilterRequest;
import com.e4u.ai_filter_service.client.dto.AiFilterResponse;
import com.e4u.ai_filter_service.common.exception.AiApiException;
import com.e4u.ai_filter_service.common.properties.AiApiProperties;
import com.e4u.ai_filter_service.domain.enums.WordRelevanceTier;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * OpenAI-based implementation of {@link AiApiClient}.
 *
 * <p>
 * Sends user-word pairs to the OpenAI chat completion API with a structured
 * prompt that instructs the model to score how relevant a vocabulary word is
 * for a <em>specific learner</em> based on their proficiency level, active
 * learning goals, and current vocabulary statistics.
 *
 * <p>
 * The model is expected to return a JSON object (enforced via
 * {@code response_format: json_object}).
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OpenAiApiClient implements AiApiClient {

    @Qualifier("aiRestClient")
    private final RestClient restClient;

    private final AiApiProperties aiApiProperties;
    private final ObjectMapper objectMapper;

    // ─── System prompt ────────────────────────────────────────────────────────

    /**
     * Instructs the model to act as a personalized vocabulary relevance scorer.
     * The response format enforces {@code relevanceTier}, {@code relevanceScore},
     * and {@code reason} fields so that parsing is straightforward.
     */
    private static final String SYSTEM_PROMPT = "You are a vocabulary relevance scoring assistant for a personalized English learning app. "
            +
            "Given a vocabulary word and a specific learner's profile, determine how relevant this word " +
            "is for the learner to study RIGHT NOW. " +
            "Use the following tier definitions: " +
            "HIGH: The word is at the right difficulty level, aligns with the learner's active goals, " +
            "and has not yet been mastered — prioritize it in lessons. " +
            "MEDIUM: The word is somewhat useful but not immediately critical (slightly off-goal or " +
            "outside the ideal difficulty window). " +
            "LOW: The word is already mastered, too advanced, too basic for the learner's level, " +
            "or completely unrelated to their current goals. " +
            "Respond ONLY with a valid JSON object in this exact format: " +
            "{\"relevanceTier\": \"HIGH|MEDIUM|LOW\", \"relevanceScore\": 0.0, \"reason\": \"brief explanation\"}";

    // ─── Single word ──────────────────────────────────────────────────────────

    @Override
    public AiFilterResponse filter(AiFilterRequest request) {
        log.debug("Scoring relevance for userId={} word='{}'", request.userId(), request.lemma());

        String userMessage = buildSingleWordPrompt(request);
        String rawResponse = callChatCompletion(userMessage);

        return parseSingleResponse(rawResponse, request);
    }

    // ─── Batch ───────────────────────────────────────────────────────────────

    @Override
    public List<AiFilterResponse> filterBatch(List<AiFilterRequest> requests) {
        log.debug("Scoring relevance for batch of {} user-word pairs", requests.size());

        String userMessage = buildBatchPrompt(requests);
        String rawResponse = callChatCompletion(userMessage);

        return parseBatchResponse(rawResponse, requests);
    }

    // ─── Prompt builders ─────────────────────────────────────────────────────

    private String buildSingleWordPrompt(AiFilterRequest request) {
        // Prefer the user-specific context sentence (from word_context_templates)
        // over the generic global_dictionary example — it shows the word in the
        // exact context the learner will actually encounter it.
        String contextToUse = (request.userContextSentence() != null && !request.userContextSentence().isBlank())
                ? "[user context] " + request.userContextSentence()
                : (request.exampleSentence() != null ? request.exampleSentence() : "N/A");

        return String.format(
                "Learner profile: level=%s | goals=%s | learned=%d words | mastered=%d words%n" +
                        "Word to score: \"%s\" | POS: %s | Definition: %s | Context: %s",
                request.proficiencyLevel() != null ? request.proficiencyLevel() : "unknown",
                request.learningGoals() != null && !request.learningGoals().isEmpty()
                        ? String.join(", ", request.learningGoals())
                        : "none",
                request.learnedWordCount(),
                request.masteredWordCount(),
                request.lemma(),
                request.partOfSpeech() != null ? request.partOfSpeech() : "unknown",
                request.definition() != null ? request.definition() : "N/A",
                contextToUse);
    }

    private String buildBatchPrompt(List<AiFilterRequest> requests) {
        StringBuilder sb = new StringBuilder(
                "Score the relevance of each vocabulary word for the given learner. " +
                        "Respond with a JSON array where each element corresponds to the same index:\n" +
                        "[{\"relevanceTier\":\"HIGH|MEDIUM|LOW\",\"relevanceScore\":0.0,\"reason\":\"...\"}, ...]\n\n");

        for (int i = 0; i < requests.size(); i++) {
            AiFilterRequest r = requests.get(i);
            // Prefer user-specific context sentence if available
            String contextToUse = (r.userContextSentence() != null && !r.userContextSentence().isBlank())
                    ? "[user context] " + r.userContextSentence()
                    : (r.exampleSentence() != null ? r.exampleSentence() : "N/A");
            sb.append(String.format(
                    "%d. [userId=%s level=%s goals=%s learned=%d mastered=%d] " +
                            "Word: \"%s\" | POS: %s | Definition: %s | Context: %s%n",
                    i + 1,
                    r.userId(),
                    r.proficiencyLevel() != null ? r.proficiencyLevel() : "unknown",
                    r.learningGoals() != null && !r.learningGoals().isEmpty()
                            ? String.join(",", r.learningGoals())
                            : "none",
                    r.learnedWordCount(),
                    r.masteredWordCount(),
                    r.lemma(),
                    r.partOfSpeech() != null ? r.partOfSpeech() : "unknown",
                    r.definition() != null ? r.definition() : "N/A",
                    contextToUse));
        }
        return sb.toString();
    }

    // ─── HTTP call ───────────────────────────────────────────────────────────

    private String callChatCompletion(String userMessage) {
        Map<String, Object> requestBody = Map.of(
                "model", aiApiProperties.getModel(),
                "response_format", Map.of("type", "json_object"),
                "messages", List.of(
                        Map.of("role", "system", "content", SYSTEM_PROMPT),
                        Map.of("role", "user", "content", userMessage)));

        try {
            String responseBody = restClient.post()
                    .uri("/v1/chat/completions")
                    .body(requestBody)
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                        throw new AiApiException("AI API client error: HTTP " + res.getStatusCode());
                    })
                    .onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
                        // 5xx is retryable — AiApiException is configured for retry in the batch step
                        throw new AiApiException("AI API server error: HTTP " + res.getStatusCode());
                    })
                    .body(String.class);

            // Extract content from OpenAI response envelope
            JsonNode root = objectMapper.readTree(responseBody);
            JsonNode content = root.path("choices").get(0).path("message").path("content");
            return content.asText();

        } catch (AiApiException e) {
            throw e;
        } catch (JsonProcessingException e) {
            throw new AiApiException("Failed to parse AI API response JSON", e);
        } catch (Exception e) {
            throw new AiApiException("Unexpected error calling AI API: " + e.getMessage(), e);
        }
    }

    // ─── Response parsers ────────────────────────────────────────────────────

    private AiFilterResponse parseSingleResponse(String rawJson, AiFilterRequest request) {
        try {
            JsonNode node = objectMapper.readTree(rawJson);
            return new AiFilterResponse(
                    request.wordId(),
                    request.userId(),
                    parseTier(node.path("relevanceTier").asText("MEDIUM")),
                    node.has("relevanceScore") ? (float) node.path("relevanceScore").asDouble(0.5) : null,
                    node.path("reason").asText(""));
        } catch (JsonProcessingException e) {
            log.warn("Could not parse AI response for word '{}' userId={}, defaulting to MEDIUM. Raw: {}",
                    request.lemma(), request.userId(), rawJson);
            return new AiFilterResponse(request.wordId(), request.userId(),
                    WordRelevanceTier.MEDIUM, null, "Parse error: " + e.getMessage());
        }
    }

    private List<AiFilterResponse> parseBatchResponse(String rawJson, List<AiFilterRequest> requests) {
        List<AiFilterResponse> results = new ArrayList<>();
        try {
            JsonNode array = objectMapper.readTree(rawJson);
            for (int i = 0; i < requests.size(); i++) {
                AiFilterRequest req = requests.get(i);
                if (i < array.size()) {
                    JsonNode node = array.get(i);
                    results.add(new AiFilterResponse(
                            req.wordId(),
                            req.userId(),
                            parseTier(node.path("relevanceTier").asText("MEDIUM")),
                            node.has("relevanceScore") ? (float) node.path("relevanceScore").asDouble(0.5) : null,
                            node.path("reason").asText("")));
                } else {
                    // AI returned fewer items than requested
                    log.warn("AI batch response missing index {} for word '{}' userId={}", i, req.lemma(),
                            req.userId());
                    results.add(new AiFilterResponse(req.wordId(), req.userId(),
                            WordRelevanceTier.MEDIUM, null, "Missing from batch response"));
                }
            }
        } catch (JsonProcessingException e) {
            log.error("Failed to parse AI batch response. Defaulting all to MEDIUM. Error: {}", e.getMessage());
            for (AiFilterRequest req : requests) {
                results.add(new AiFilterResponse(req.wordId(), req.userId(),
                        WordRelevanceTier.MEDIUM, null, "Batch parse error"));
            }
        }
        return results;
    }

    private WordRelevanceTier parseTier(String raw) {
        try {
            return WordRelevanceTier.valueOf(raw.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            log.warn("Unknown relevance tier '{}' from AI — defaulting to MEDIUM", raw);
            return WordRelevanceTier.MEDIUM;
        }
    }
}
