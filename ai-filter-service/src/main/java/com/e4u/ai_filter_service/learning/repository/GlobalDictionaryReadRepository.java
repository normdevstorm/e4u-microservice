package com.e4u.ai_filter_service.learning.repository;

import com.e4u.ai_filter_service.learning.entity.GlobalDictionaryReadOnly;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Read repository for {@link GlobalDictionaryReadOnly} on the
 * {@code e4u_learning} DB.
 *
 * <p>
 * Backed by {@code learningEntityManager} / {@code learningTransactionManager}.
 * All operations are strictly read-only — no writes to
 * {@code global_dictionary}
 * are permitted from ai-filter-service.
 *
 * <p>
 * The relevance write-back is handled by
 * {@link UserVocabProgressReadRepository}
 * (targeting {@code user_vocab_progress.relevance_score}) — NOT by this
 * repository.
 */
@Repository
@Transactional(readOnly = true, transactionManager = "learningTransactionManager")
public interface GlobalDictionaryReadRepository extends JpaRepository<GlobalDictionaryReadOnly, UUID> {

    /**
     * Find a paginated view of all non-deleted words.
     * Used for admin lookups — the batch reader uses JDBC directly for performance.
     */
    @Query("SELECT g FROM GlobalDictionaryReadOnly g WHERE g.deleted = false")
    Page<GlobalDictionaryReadOnly> findAllActive(Pageable pageable);

    /**
     * Count all non-deleted words in the global dictionary.
     * Used for reporting / admin dashboards.
     */
    @Query("SELECT COUNT(g) FROM GlobalDictionaryReadOnly g WHERE g.deleted = false")
    long countActiveWords();

    /**
     * Look up a specific word by its lemma (case-insensitive).
     * Used for ad-hoc word lookups from the management API.
     */
    @Query("SELECT g FROM GlobalDictionaryReadOnly g WHERE LOWER(g.lemma) = LOWER(:lemma) AND g.deleted = false")
    java.util.Optional<GlobalDictionaryReadOnly> findByLemmaIgnoreCase(@Param("lemma") String lemma);
}
