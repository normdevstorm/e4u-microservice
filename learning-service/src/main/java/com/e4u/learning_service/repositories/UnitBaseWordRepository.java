package com.e4u.learning_service.repositories;

import com.e4u.learning_service.entities.UnitBaseWord;
import com.e4u.learning_service.entities.UnitBaseWordId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UnitBaseWordRepository
                extends JpaRepository<UnitBaseWord, UnitBaseWordId>, JpaSpecificationExecutor<UnitBaseWord> {

        // Find all (non-deleted)
        Page<UnitBaseWord> findByDeletedFalse(Pageable pageable);

        // Find by unit ID
        List<UnitBaseWord> findByUnitIdAndDeletedFalseOrderBySequenceOrderAsc(UUID unitId);

        // Find by unit ID with pagination
        Page<UnitBaseWord> findByUnitIdAndDeletedFalse(UUID unitId, Pageable pageable);

        // Find by word ID
        List<UnitBaseWord> findByWordIdAndDeletedFalse(UUID wordId);

        // Find specific unit-word combination
        Optional<UnitBaseWord> findByUnitIdAndWordIdAndDeletedFalse(UUID unitId, UUID wordId);

        // Find with word details
        @Query("SELECT ubw FROM UnitBaseWord ubw JOIN FETCH ubw.word w WHERE ubw.unitId = :unitId AND ubw.deleted = false AND w.deleted = false ORDER BY ubw.sequenceOrder ASC")
        List<UnitBaseWord> findByUnitIdWithWordDetails(@Param("unitId") UUID unitId);

        // Soft delete by unit and word
        @Modifying
        @Query("UPDATE UnitBaseWord ubw SET ubw.deleted = true, ubw.deletedAt = :deletedAt WHERE ubw.unitId = :unitId AND ubw.wordId = :wordId")
        int softDeleteByUnitIdAndWordId(@Param("unitId") UUID unitId, @Param("wordId") UUID wordId,
                        @Param("deletedAt") Instant deletedAt);

        // Hard delete by unit and word (permanent removal)
        @Modifying
        @Query("DELETE FROM UnitBaseWord ubw WHERE ubw.unitId = :unitId AND ubw.wordId = :wordId")
        int hardDeleteByUnitIdAndWordId(@Param("unitId") UUID unitId, @Param("wordId") UUID wordId);

        // Soft delete all words in a unit
        @Modifying
        @Query("UPDATE UnitBaseWord ubw SET ubw.deleted = true, ubw.deletedAt = :deletedAt WHERE ubw.unitId = :unitId")
        int softDeleteByUnitId(@Param("unitId") UUID unitId, @Param("deletedAt") Instant deletedAt);

        // Check if word exists in unit
        boolean existsByUnitIdAndWordIdAndDeletedFalse(UUID unitId, UUID wordId);

        // Get max sequence order in a unit
        @Query("SELECT COALESCE(MAX(ubw.sequenceOrder), 0) FROM UnitBaseWord ubw WHERE ubw.unitId = :unitId AND ubw.deleted = false")
        Integer findMaxSequenceOrderByUnitId(@Param("unitId") UUID unitId);

        // Count words in unit
        @Query("SELECT COUNT(ubw) FROM UnitBaseWord ubw WHERE ubw.unitId = :unitId AND ubw.deleted = false")
        long countByUnitId(@Param("unitId") UUID unitId);

}
