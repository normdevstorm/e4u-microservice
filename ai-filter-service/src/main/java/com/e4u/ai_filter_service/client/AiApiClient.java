package com.e4u.ai_filter_service.client;

import com.e4u.ai_filter_service.client.dto.AiFilterRequest;
import com.e4u.ai_filter_service.client.dto.AiFilterResponse;

import java.util.List;

/**
 * Contract for the AI word filtration API client.
 *
 * <p>
 * Implementations are expected to be retried on transient failures
 * ({@link com.e4u.ai_filter_service.common.exception.AiApiException})
 * as configured in the Spring Batch step's fault-tolerance settings.
 */
public interface AiApiClient {

    /**
     * Filter a single word through the AI model.
     *
     * @param request word data to evaluate
     * @return classification result with reason and confidence
     * @throws com.e4u.ai_filter_service.common.exception.AiApiException on HTTP
     *                                                                   errors or
     *                                                                   parse
     *                                                                   failures
     */
    AiFilterResponse filter(AiFilterRequest request);

    /**
     * Filter a batch of words in a single AI API call.
     * More efficient than calling {@link #filter(AiFilterRequest)} individually.
     *
     * @param requests list of words to evaluate (size capped by
     *                 {@code ai.api.batch-size})
     * @return list of results in the same order as the input
     * @throws com.e4u.ai_filter_service.common.exception.AiApiException on HTTP
     *                                                                   errors or
     *                                                                   parse
     *                                                                   failures
     */
    List<AiFilterResponse> filterBatch(List<AiFilterRequest> requests);
}
