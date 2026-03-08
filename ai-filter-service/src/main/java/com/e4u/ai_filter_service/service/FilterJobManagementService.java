package com.e4u.ai_filter_service.service;

import com.e4u.ai_filter_service.domain.entity.FilterJobItem;
import com.e4u.ai_filter_service.domain.enums.TriggerType;
import com.e4u.ai_filter_service.domain.enums.WordRelevanceTier;
import com.e4u.ai_filter_service.learning.repository.WordContextTemplateReadRepository;
import com.e4u.ai_filter_service.repository.FilterJobItemRepository;
import com.e4u.ai_filter_service.scheduler.FilterJobScheduler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Service for managing AI filter batch job executions.
 *
 * <p>
 * Provides:
 * <ul>
 * <li>Manual job trigger (via REST API)</li>
 * <li>Job execution status queries (via Spring Batch {@link JobExplorer})</li>
 * <li>Per-user relevance result queries</li>
 * <li>Pending template count (queue depth monitoring)</li>
 * <li>Job stop operations</li>
 * </ul>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FilterJobManagementService {

    private final FilterJobScheduler filterJobScheduler;
    private final JobExplorer jobExplorer;
    private final JobOperator jobOperator;
    private final FilterJobItemRepository filterJobItemRepository;
    private final WordContextTemplateReadRepository wordContextTemplateReadRepository;

    // ─── Trigger ─────────────────────────────────────────────────────────────

    /**
     * Manually triggers a new job execution (async via batchTaskExecutor).
     * Returns immediately — job runs in background thread.
     */
    public void triggerManual() {
        log.info("Manual job trigger requested via REST API");
        // TODO: run asynchronously using batchTaskExecutor to avoid blocking HTTP
        // thread
        filterJobScheduler.launchJob(TriggerType.MANUAL);
    }

    // ─── Status queries ──────────────────────────────────────────────────────

    /**
     * Get all executions of the wordFilterJob, most recent first.
     */
    public List<JobExecution> listExecutions() {
        Set<JobExecution> executions = jobExplorer.findRunningJobExecutions("wordFilterJob");
        // TODO: return paginated results including completed executions from
        // jobExplorer
        return List.copyOf(executions);
    }

    /**
     * Get a specific job execution by ID.
     */
    public JobExecution getExecution(Long executionId) {
        JobExecution execution = jobExplorer.getJobExecution(executionId);
        if (execution == null) {
            throw new IllegalArgumentException("JobExecution not found for id=" + executionId);
        }
        return execution;
    }

    // ─── Per-execution results ────────────────────────────────────────────────

    /**
     * Get all per-word relevance results for a specific job execution (paginated).
     */
    @Transactional(readOnly = true)
    public Page<FilterJobItem> getItems(Long executionId, Pageable pageable) {
        return filterJobItemRepository.findByJobExecutionId(executionId, pageable);
    }

    // ─── Per-user relevance results ───────────────────────────────────────────

    /**
     * Get all relevance results for a specific user (paginated).
     * Ordered by most recently processed first.
     */
    @Transactional(readOnly = true)
    public Page<FilterJobItem> getItemsByUser(UUID userId, Pageable pageable) {
        return filterJobItemRepository.findByUserId(userId, pageable);
    }

    /**
     * Get HIGH-relevance results for a specific user.
     * These are the words recommended for immediate inclusion in lessons.
     */
    @Transactional(readOnly = true)
    public Page<FilterJobItem> getHighRelevanceItems(UUID userId, Pageable pageable) {
        return filterJobItemRepository.findByUserIdAndRelevanceTier(userId, WordRelevanceTier.HIGH, pageable);
    }

    /**
     * Get LOW-relevance results for a specific user.
     * These are the words the AI determined are not suitable for the learner right
     * now.
     */
    @Transactional(readOnly = true)
    public Page<FilterJobItem> getLowRelevanceItems(UUID userId, Pageable pageable) {
        return filterJobItemRepository.findByUserIdAndRelevanceTier(userId, WordRelevanceTier.LOW, pageable);
    }

    /**
     * Get the full AI filter history for a specific word across all users.
     */
    @Transactional(readOnly = true)
    public List<FilterJobItem> getWordHistory(UUID wordId) {
        return filterJobItemRepository.findByWordIdOrderByProcessedAtDesc(wordId);
    }

    // ─── Queue depth ──────────────────────────────────────────────────────────

    /**
     * Count user-specific {@code word_context_templates} rows that have not yet
     * been evaluated by the AI batch job (i.e. {@code ai_reasoning IS NULL}).
     *
     * <p>
     * This is the queue depth — how many (user, word) pairs are waiting for
     * the next batch run.
     */
    public long countPendingTemplates() {
        return wordContextTemplateReadRepository.countAllPending();
    }

    /**
     * Count pending templates for a specific user.
     */
    public long countPendingTemplatesByUser(UUID userId) {
        return wordContextTemplateReadRepository.countPendingByUser(userId);
    }

    // ─── Stop ─────────────────────────────────────────────────────────────────

    /**
     * Request a graceful stop of a running job execution.
     * The job will stop after completing the current chunk.
     */
    public void stopExecution(Long executionId) {
        try {
            boolean stopped = jobOperator.stop(executionId);
            log.info("Stop requested for jobExecutionId={}, accepted={}", executionId, stopped);
        } catch (Exception e) {
            log.error("Failed to stop jobExecutionId={}: {}", executionId, e.getMessage());
            throw new IllegalStateException("Could not stop job execution: " + e.getMessage(), e);
        }
    }
}
