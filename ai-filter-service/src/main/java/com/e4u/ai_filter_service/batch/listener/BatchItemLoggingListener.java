package com.e4u.ai_filter_service.batch.listener;

import com.e4u.ai_filter_service.batch.domain.UserWordPair;
import com.e4u.ai_filter_service.domain.entity.FilterJobItem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ItemReadListener;
import org.springframework.batch.core.ItemWriteListener;
import org.springframework.batch.item.Chunk;
import org.springframework.stereotype.Component;

/**
 * Spring Batch listener that logs each item read from {@code e4u_learning}
 * and each chunk written to {@code e4u_ai_filter}.
 *
 * <p>
 * Registered on the step via {@code .listener(batchItemLoggingListener)}.
 * Uses DEBUG for normal flow and WARN for errors, so production
 * {@code logging.level.com.e4u.ai_filter_service: info} stays quiet while
 * {@code debug} surfaces the full pipeline trace.
 */
@Slf4j
@Component
public class BatchItemLoggingListener
        implements ItemReadListener<UserWordPair>, ItemWriteListener<FilterJobItem> {

    // ─── Reader side ──────────────────────────────────────────────────────────

    @Override
    public void beforeRead() {
        log.trace("Reader: about to read next item");
    }

    /**
     * Called after each successful {@code read()} — includes items that returned
     * {@code null} (end-of-input sentinel).
     */
    @Override
    public void afterRead(UserWordPair item) {
        if (item == null) {
            // null = end of input, not an error
            log.debug("Reader: end of input (null returned)");
            return;
        }
        log.debug("Reader: read item — userId={} wordId={} lemma='{}' partOfSpeech='{}' templateId={}",
                item.userId(),
                item.wordId(),
                item.word() != null ? item.word().getLemma() : "N/A",
                item.word() != null ? item.word().getPartOfSpeech() : "N/A",
                item.contextTemplateId());
    }

    /**
     * Called when {@code read()} throws an exception (e.g. JDBC error, row-mapper
     * failure). The item is skipped if the step's {@code skipLimit} allows it.
     */
    @Override
    public void onReadError(Exception ex) {
        log.warn("Reader: error reading item — {}: {}", ex.getClass().getSimpleName(), ex.getMessage());
    }

    // ─── Writer side ──────────────────────────────────────────────────────────

    /**
     * Logs the full chunk summary before it hits the database so you can correlate
     * what was sent vs. what was persisted.
     */
    @Override
    public void beforeWrite(Chunk<? extends FilterJobItem> chunk) {
        if (!log.isDebugEnabled())
            return;

        log.debug("Writer: writing chunk of {} item(s)", chunk.size());
        chunk.getItems().forEach(item -> log.debug("  → userId={} wordId={} lemma='{}' tier={} score={} templateId={}",
                item.getUserId(),
                item.getWordId(),
                item.getWordLemma(),
                item.getRelevanceTier(),
                item.getRelevanceScore(),
                item.getWordContextTemplateId()));
    }

    /**
     * Logs confirmation after all items in the chunk are persisted successfully.
     */
    @Override
    public void afterWrite(Chunk<? extends FilterJobItem> chunk) {
        log.debug("Writer: chunk of {} item(s) written successfully", chunk.size());
    }

    /**
     * Called when the entire chunk write fails. Individual item failures inside
     * {@code FilterJobItemWriter} are handled there; this catches infrastructure
     * failures (e.g. DB down, transaction rollback).
     */
    @Override
    public void onWriteError(Exception ex, Chunk<? extends FilterJobItem> chunk) {
        log.warn("Writer: chunk write failed for {} item(s) — {}: {}",
                chunk.size(), ex.getClass().getSimpleName(), ex.getMessage());

        if (log.isDebugEnabled()) {
            chunk.getItems().forEach(item -> log.debug("  ✗ failed item: userId={} wordId={} lemma='{}'",
                    item.getUserId(), item.getWordId(), item.getWordLemma()));
        }
    }
}
