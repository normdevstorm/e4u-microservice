package com.e4u.ai_filter_service.repository;

import com.e4u.ai_filter_service.domain.entity.FilterJobItem;
import com.e4u.ai_filter_service.domain.enums.WordRelevanceTier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository for {@link FilterJobItem}.
 * Backed by the {@code aiFilterEntityManager} → {@code e4u_ai_filter} DB.
 */
@Repository
public interface FilterJobItemRepository extends JpaRepository<FilterJobItem, UUID> {

    /** Retrieve all per-user-word results for a specific batch job execution. */
    Page<FilterJobItem> findByJobExecutionId(Long jobExecutionId, Pageable pageable);

    /**
     * Count results by relevance tier for a given execution (job summary stats).
     */
    long countByJobExecutionIdAndRelevanceTier(Long jobExecutionId, WordRelevanceTier relevanceTier);

    /** Find all scored items for a specific user (paginated). */
    Page<FilterJobItem> findByUserId(UUID userId, Pageable pageable);

    /** Find all scored items for a specific user filtered by relevance tier. */
    Page<FilterJobItem> findByUserIdAndRelevanceTier(UUID userId, WordRelevanceTier relevanceTier, Pageable pageable);

    /**
     * Find all items with a given relevance tier across all users/executions (admin
     * view).
     */
    Page<FilterJobItem> findByRelevanceTier(WordRelevanceTier relevanceTier, Pageable pageable);

    /** Check if a (user, word) pair was already scored in any execution. */
    boolean existsByUserIdAndWordId(UUID userId, UUID wordId);

    /** Get the full scoring history for a specific word across all users. */
    List<FilterJobItem> findByWordIdOrderByProcessedAtDesc(UUID wordId);

    /**
     * Get the scoring history for a specific (user, word) pair (most recent first).
     */
    List<FilterJobItem> findByUserIdAndWordIdOrderByProcessedAtDesc(UUID userId, UUID wordId);
}
