package com.e4u.curriculum_service.repositories;

import com.e4u.curriculum_service.entities.GlobalDictionary;
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
public interface GlobalDictionaryRepository
                extends JpaRepository<GlobalDictionary, UUID>, JpaSpecificationExecutor<GlobalDictionary> {

        // Find by ID (non-deleted)
        Optional<GlobalDictionary> findByIdAndDeletedFalse(UUID id);

        // Find all (non-deleted)
        Page<GlobalDictionary> findByDeletedFalse(Pageable pageable);

        // Find by lemma (exact match)
        Optional<GlobalDictionary> findByLemmaAndDeletedFalse(String lemma);

        // Find by lemma (case insensitive)
        @Query("SELECT g FROM GlobalDictionary g WHERE LOWER(g.lemma) = LOWER(:lemma) AND g.deleted = false")
        Optional<GlobalDictionary> findByLemmaIgnoreCaseAndDeletedFalse(@Param("lemma") String lemma);

        // Search by lemma (contains)
        @Query("SELECT g FROM GlobalDictionary g WHERE LOWER(g.lemma) LIKE LOWER(CONCAT('%', :keyword, '%')) AND g.deleted = false")
        Page<GlobalDictionary> searchByLemma(@Param("keyword") String keyword, Pageable pageable);

        // Find by part of speech
        Page<GlobalDictionary> findByPartOfSpeechAndDeletedFalse(String partOfSpeech, Pageable pageable);

        // Find by difficulty level
        Page<GlobalDictionary> findByDifficultyLevelAndDeletedFalse(String difficultyLevel, Pageable pageable);

        // Find with translations
        @Query("SELECT DISTINCT g FROM GlobalDictionary g LEFT JOIN FETCH g.translations WHERE g.id = :id AND g.deleted = false")
        Optional<GlobalDictionary> findByIdWithTranslations(@Param("id") UUID id);

        // Find multiple by IDs
        @Query("SELECT g FROM GlobalDictionary g WHERE g.id IN :ids AND g.deleted = false")
        List<GlobalDictionary> findByIdInAndDeletedFalse(@Param("ids") List<UUID> ids);

        // Soft delete
        @Modifying
        @Query("UPDATE GlobalDictionary g SET g.deleted = true, g.deletedAt = :deletedAt WHERE g.id = :id")
        int softDeleteById(@Param("id") UUID id, @Param("deletedAt") Instant deletedAt);

        // Check if lemma exists
        boolean existsByLemmaAndDeletedFalse(String lemma);

        // Find by frequency range
        @Query("SELECT g FROM GlobalDictionary g WHERE g.frequencyScore BETWEEN :minScore AND :maxScore AND g.deleted = false")
        Page<GlobalDictionary> findByFrequencyScoreRange(@Param("minScore") Float minScore,
                        @Param("maxScore") Float maxScore, Pageable pageable);
}
