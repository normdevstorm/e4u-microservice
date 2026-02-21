package com.e4u.curriculum_service.repositories;

import com.e4u.curriculum_service.entities.TranslationDict;
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
public interface TranslationDictRepository
        extends JpaRepository<TranslationDict, UUID>, JpaSpecificationExecutor<TranslationDict> {

    // Find by ID (non-deleted)
    Optional<TranslationDict> findByIdAndDeletedFalse(UUID id);

    // Find all (non-deleted)
    Page<TranslationDict> findByDeletedFalse(Pageable pageable);

    // Find by word ID
    List<TranslationDict> findByWord_IdAndDeletedFalse(UUID wordId);

    // Find by word ID and language
    Optional<TranslationDict> findByWord_IdAndDestLangAndDeletedFalse(UUID wordId, String destLang);

    // Find by language
    Page<TranslationDict> findByDestLangAndDeletedFalse(String destLang, Pageable pageable);

    // Search translations
    @Query("SELECT t FROM TranslationDict t WHERE LOWER(t.translation) LIKE LOWER(CONCAT('%', :keyword, '%')) AND t.deleted = false")
    Page<TranslationDict> searchByTranslation(@Param("keyword") String keyword, Pageable pageable);

    // Soft delete
    @Modifying
    @Query("UPDATE TranslationDict t SET t.deleted = true, t.deletedAt = :deletedAt WHERE t.id = :id")
    int softDeleteById(@Param("id") UUID id, @Param("deletedAt") Instant deletedAt);

    // Soft delete all translations for a word
    @Modifying
    @Query("UPDATE TranslationDict t SET t.deleted = true, t.deletedAt = :deletedAt WHERE t.word.id = :wordId")
    int softDeleteByWordId(@Param("wordId") UUID wordId, @Param("deletedAt") Instant deletedAt);

    // Check if translation exists for word and language
    boolean existsByWord_IdAndDestLangAndDeletedFalse(UUID wordId, String destLang);
}
