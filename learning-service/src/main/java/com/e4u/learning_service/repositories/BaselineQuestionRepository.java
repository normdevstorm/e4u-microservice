package com.e4u.learning_service.repositories;

import com.e4u.learning_service.entities.BaselineQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BaselineQuestionRepository extends JpaRepository<BaselineQuestion, UUID> {

    /**
     * All questions ordered by cefr_tier then sort_order (A1 → C1).
     * Used to serve the full placement test.
     */
    List<BaselineQuestion> findAllByOrderByCefrTierAscSortOrderAsc();

    /**
     * Questions for a single CEFR tier — useful for targeted retests.
     */
    List<BaselineQuestion> findByCefrTierOrderBySortOrderAsc(String cefrTier);
}
