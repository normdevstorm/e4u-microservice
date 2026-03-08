package com.e4u.ai_filter_service.batch.processor;

import com.e4u.ai_filter_service.batch.domain.UserContext;
import com.e4u.ai_filter_service.batch.domain.UserWordPair;
import com.e4u.ai_filter_service.client.AiApiClient;
import com.e4u.ai_filter_service.client.dto.AiFilterRequest;
import com.e4u.ai_filter_service.client.dto.AiFilterResponse;
import com.e4u.ai_filter_service.common.exception.AiApiException;
import com.e4u.ai_filter_service.domain.entity.FilterJobItem;
import com.e4u.ai_filter_service.domain.enums.WordRelevanceTier;
import com.e4u.ai_filter_service.learning.entity.GlobalDictionaryReadOnly;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.time.Instant;

/**
 * Processes each (user, word) pair by calling the AI relevance-scoring API and
 * mapping the result to a {@link FilterJobItem} ready for persistence.
 *
 * <p>
 * Input: {@link UserWordPair} — contains the word details and the pre-fetched
 * {@link UserContext} (proficiency level, learning goals, vocab stats).
 *
 * <p>
 * Output: {@link FilterJobItem} — persisted to
 * {@code e4u_ai_filter.filter_job_items}
 * by the writer.
 *
 * <p>
 * Retry behaviour (up to 3 times on {@link AiApiException}) is configured in
 * {@code WordFilterJobConfig}. This processor just calls and maps.
 */
@Slf4j
@Component
@StepScope
@RequiredArgsConstructor
public class WordFilterItemProcessor implements ItemProcessor<UserWordPair, FilterJobItem> {

        private final AiApiClient aiApiClient;

        /**
         * Injected from StepExecution context — links FilterJobItem to the batch run.
         */
        private Long jobExecutionId;

        @BeforeStep
        public void beforeStep(StepExecution stepExecution) {
                this.jobExecutionId = stepExecution.getJobExecutionId();
                log.info("WordFilterItemProcessor initialized for jobExecutionId={}", jobExecutionId);
        }

        @Override
        public FilterJobItem process(UserWordPair pair) throws Exception {
                GlobalDictionaryReadOnly word = pair.word();
                UserContext ctx = pair.userContext();

                log.debug("Scoring relevance: userId={} word='{}' (id={})",
                                pair.userId(), word.getLemma(), word.getId());

                // Build AI request with full user context + user-specific context sentence
                AiFilterRequest request = new AiFilterRequest(
                                word.getId(),
                                word.getLemma(),
                                word.getPartOfSpeech(),
                                word.getDefinition(),
                                word.getExampleSentence(), // generic fallback
                                pair.userContextSentence(), // user-specific sentence from word_context_templates
                                pair.userId(),
                                ctx.proficiencyLevel(),
                                ctx.learningGoalNames(),
                                ctx.learnedWordCount(),
                                ctx.masteredWordCount());

                // Call AI API — AiApiException is retryable per batch step config
                AiFilterResponse response = aiApiClient.filter(request);

                FilterJobItem item = FilterJobItem.builder()
                                .jobExecutionId(jobExecutionId)
                                .userId(pair.userId())
                                .wordId(word.getId())
                                .wordLemma(word.getLemma())
                                // TOOD: HARDCODED FOR NOW — uncomment when AI API is wired up
                                .relevanceTier(WordRelevanceTier.HIGH)
                                .relevanceScore(0.95f)
                                // .relevanceTier(response.relevanceTier())
                                // .relevanceScore(response.relevanceScore())
                                // .aiReason(response.reason())
                                .wordContextTemplateId(pair.contextTemplateId()) // link to source template
                                .processedAt(Instant.now())
                                .build();

                // log.debug("userId={} word='{}' → {} (score={})",
                // pair.userId(), word.getLemma(), response.relevanceTier(),
                // response.relevanceScore());

                return item;
        }
}
