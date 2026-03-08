package com.e4u.ai_filter_service.batch.writer;

import com.e4u.ai_filter_service.domain.entity.FilterJobItem;
import com.e4u.ai_filter_service.domain.enums.WordRelevanceTier;
import com.e4u.ai_filter_service.learning.repository.UserVocabProgressReadRepository;
import com.e4u.ai_filter_service.learning.repository.WordContextTemplateReadRepository;
import com.e4u.ai_filter_service.repository.FilterJobItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

/**
 * Writes a chunk of AI-scored {@link FilterJobItem} results and performs
 * two write-backs into {@code e4u_learning}:
 *
 * <ol>
 * <li><strong>Primary persistence</strong>: saves all {@link FilterJobItem}
 * rows
 * to {@code e4u_ai_filter.filter_job_items} (transactional).</li>
 * <li><strong>Curriculum write-back</strong>: for every item, calls
 * {@code word_context_templates.updateAfterAiScoring(id, selected, reason)}:
 * <ul>
 * <li>HIGH or MEDIUM tier → {@code is_selected_by_ai = true}
 * (word is promoted into the user's curriculum unit)</li>
 * <li>LOW tier → {@code is_selected_by_ai = false}
 * (word rejected, but {@code ai_reasoning} is set so it won't be
 * re-evaluated on the next batch run)</li>
 * </ul>
 * This is <strong>best-effort</strong>: individual failures are logged as
 * warnings and do not roll back the primary write. The template row will
 * be re-evaluated on the next run only if {@code ai_reasoning} remains
 * NULL.</li>
 * <li><strong>Secondary write-back</strong>: propagates the
 * {@code relevance_score}
 * to {@code user_vocab_progress.relevance_score} for lesson-ranking use
 * in the learning service. Also best-effort.</li>
 * </ol>
 */
@Slf4j
@Component
@StepScope
@RequiredArgsConstructor
public class FilterJobItemWriter implements ItemWriter<FilterJobItem> {

    private final FilterJobItemRepository filterJobItemRepository;
    private final WordContextTemplateReadRepository wordContextTemplateReadRepository;
    private final UserVocabProgressReadRepository userVocabProgressReadRepository;

    @Override
    public void write(Chunk<? extends FilterJobItem> chunk) throws Exception {
        log.debug("Writing chunk of {} filter job items", chunk.size());

        // PRIMARY: save all scored items to e4u_ai_filter (transactional)
        filterJobItemRepository.saveAll(chunk.getItems());
        log.debug("Saved {} FilterJobItems to e4u_ai_filter", chunk.size());

        // CURRICULUM WRITE-BACK: flip is_selected_by_ai in e4u_learning (best-effort)
        writeCurriculumSelectionBack(chunk);

        // SECONDARY WRITE-BACK: propagate relevance_score to user_vocab_progress
        // (best-effort)
        // writeRelevanceScoreBack(chunk);
    }

    /**
     * For each processed item, calls {@code updateAfterAiScoring} on the source
     * {@code word_context_templates} row:
     * <ul>
     * <li>HIGH / MEDIUM → {@code is_selected_by_ai = true} (curriculum
     * insertion)</li>
     * <li>LOW → {@code is_selected_by_ai = false} (rejected, but marked done)</li>
     * </ul>
     *
     * <p>
     * Items with a null {@code wordContextTemplateId} are skipped (legacy /
     * non-template source).
     * Failures per item are non-fatal — logged as warnings.
     */
    private void writeCurriculumSelectionBack(Chunk<? extends FilterJobItem> chunk) {
        int successCount = 0;

        for (FilterJobItem item : chunk.getItems()) {
            if (item.getWordContextTemplateId() == null) {
                log.debug("Skipping curriculum write-back for userId={} wordId={} — no wordContextTemplateId",
                        item.getUserId(), item.getWordId());
                continue;
            }

            // HIGH or MEDIUM → accept into curriculum; LOW → reject (still mark as
            // processed)
            boolean selected = item.getRelevanceTier() == WordRelevanceTier.HIGH
                    || item.getRelevanceTier() == WordRelevanceTier.MEDIUM;

            // Always write a non-null ai_reasoning so the row is excluded from future runs
            String reason = item.getAiReason() != null ? item.getAiReason()
                    : item.getRelevanceTier().name() + " (no reason returned by AI)";

            try {
                wordContextTemplateReadRepository.updateAfterAiScoring(
                        item.getWordContextTemplateId(), selected, reason, "ai-filter-service");
                successCount++;

                log.debug("Curriculum write-back: templateId={} userId={} word='{}' tier={} selected={}",
                        item.getWordContextTemplateId(), item.getUserId(),
                        item.getWordLemma(), item.getRelevanceTier(), selected);
            } catch (Exception e) {
                // Non-fatal: if ai_reasoning stays NULL, the row will be re-evaluated next run.
                log.warn("Curriculum write-back failed for templateId={} userId={} word='{}': {}",
                        item.getWordContextTemplateId(), item.getUserId(),
                        item.getWordLemma(), e.getMessage());
            }
        }

        log.debug("Curriculum write-back to e4u_learning: {}/{} succeeded", successCount, chunk.size());
    }

    /**
     * Propagates the AI relevance score to
     * {@code user_vocab_progress.relevance_score}
     * so the learning service can use it for lesson content ranking without calling
     * ai-filter-service at runtime.
     *
     * <p>
     * Items with null {@code relevanceScore} are skipped to avoid overwriting
     * an existing score with null.
     */
    private void writeRelevanceScoreBack(Chunk<? extends FilterJobItem> chunk) {
        int successCount = 0;

        for (FilterJobItem item : chunk.getItems()) {
            if (item.getRelevanceScore() == null) {
                log.debug("Skipping relevance score write-back for userId={} wordId={} — score is null",
                        item.getUserId(), item.getWordId());
                continue;
            }

            try {
                userVocabProgressReadRepository.writeBackRelevanceScore(
                        item.getUserId(), item.getWordId(), item.getRelevanceScore());
                successCount++;
            } catch (Exception e) {
                // Non-fatal — the primary result in e4u_ai_filter is the source of truth
                log.warn("Relevance score write-back failed for userId={} wordId='{}' (lemma='{}'): {}",
                        item.getUserId(), item.getWordId(), item.getWordLemma(), e.getMessage());
            }
        }

        log.debug("Relevance score write-back to e4u_learning: {}/{} succeeded", successCount, chunk.size());
    }
}
