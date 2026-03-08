package com.e4u.ai_filter_service.learning.repository;

import com.e4u.ai_filter_service.learning.entity.WordContextTemplateReadOnly;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Repository for {@link WordContextTemplateReadOnly}, backed by the
 * {@code learningEntityManager} (secondary DataSource → {@code e4u_learning}).
 *
 * <p>
 * Primary responsibilities in the AI filter batch pipeline:
 * <ol>
 * <li><strong>Read</strong>: count pending templates (AI filter not yet
 * run)</li>
 * <li><strong>Write-back</strong>: after AI scoring, record the decision and
 * optionally promote the word into the curriculum unit by setting
 * {@code is_selected_by_ai = true}</li>
 * </ol>
 *
 * <p>
 * The reader ({@code GlobalDictionaryItemReader}) does NOT use this repository
 * for iteration — it uses a raw {@link javax.sql.DataSource} JDBC paging reader
 * for performance. This repository is only used for the write-back and count
 * queries.
 *
 * <p>
 * <strong>Write-back semantics</strong>:
 * <ul>
 * <li>HIGH / MEDIUM tier → {@code is_selected_by_ai = true} (word enters
 * curriculum)</li>
 * <li>LOW tier → {@code is_selected_by_ai = false} (word rejected, not
 * re-evaluated)</li>
 * <li>Always → {@code ai_reasoning} is set so the row is excluded from future
 * runs</li>
 * </ul>
 */
public interface WordContextTemplateReadRepository
                extends JpaRepository<WordContextTemplateReadOnly, UUID> {

        // ─── Write-back ──────────────────────────────────────────────────────────

        /**
         * Records the AI scoring decision for a single {@code word_context_templates}
         * row.
         *
         * <p>
         * Called by {@code FilterJobItemWriter} for every processed item:
         * <ul>
         * <li>If the AI tier was HIGH or MEDIUM → pass {@code selected = true} to
         * insert the word into the user's curriculum unit.</li>
         * <li>If the AI tier was LOW → pass {@code selected = false}.
         * The word is marked as evaluated (via {@code ai_reasoning}) so it will
         * not be re-read by future batch runs.</li>
         * </ul>
         *
         * @param id       UUID of the {@code word_context_templates} row to update
         * @param selected {@code true} if the word should be active in its curriculum
         *                 unit
         * @param reason   AI reasoning string (must be non-null to mark as processed)
         */
        @Modifying
        @Transactional
        @Query(value = """
                        UPDATE word_context_templates
                           SET is_selected_by_ai = :selected,
                               ai_reasoning      = :reason,
                               updated_at        = NOW(),
                               updated_by        = :updatedBy
                         WHERE id = :id
                        """, nativeQuery = true)
        void updateAfterAiScoring(
                        @Param("id") UUID id,
                        @Param("selected") boolean selected,
                        @Param("reason") String reason,
                        @Param("updatedBy") String updatedBy);

        // ─── Count queries ───────────────────────────────────────────────────────

        /**
         * Counts the total number of user-specific context templates that have not
         * yet been evaluated by the AI batch job.
         *
         * <p>
         * Used by {@code FilterJobManagementService.countPendingTemplates()} to report
         * how many templates are queued for the next batch run.
         *
         * <p>
         * The condition {@code ai_reasoning IS NULL} is the "not yet processed" gate —
         * after processing, every row has {@code ai_reasoning} set (even if LOW tier).
         */
        @Query("""
                        SELECT COUNT(wct) FROM WordContextTemplateReadOnly wct
                         WHERE wct.createdForUserId IS NOT NULL
                           AND wct.aiReasoning IS NULL
                        """)
        long countAllPending();

        /**
         * Counts pending templates for a specific user.
         * Useful for per-user queue size reporting in the management API.
         */
        @Query("""
                        SELECT COUNT(wct) FROM WordContextTemplateReadOnly wct
                         WHERE wct.createdForUserId = :userId
                           AND wct.aiReasoning IS NULL
                        """)
        long countPendingByUser(@Param("userId") UUID userId);
}
