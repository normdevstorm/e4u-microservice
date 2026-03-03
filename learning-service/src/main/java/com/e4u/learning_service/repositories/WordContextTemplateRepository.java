package com.e4u.learning_service.repositories;

import com.e4u.learning_service.entities.WordContextTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for WordContextTemplate entity.
 * Provides access to word contexts - both shared and user-specific.
 */
@Repository
public interface WordContextTemplateRepository extends JpaRepository<WordContextTemplate, UUID> {

    /**
     * Find all contexts for a specific word (shared + user-specific)
     */
    @Query("SELECT wct FROM WordContextTemplate wct " +
            "WHERE wct.word.id = :wordId " +
            "AND (wct.createdForUserId IS NULL OR wct.createdForUserId = :userId)")
    List<WordContextTemplate> findByWordIdForUser(
            @Param("wordId") UUID wordId,
            @Param("userId") UUID userId);

    /**
     * Find only shared/system contexts for a word
     */
    List<WordContextTemplate> findByWordIdAndCreatedForUserIdIsNull(UUID wordId);

    /**
     * Find user-specific contexts for a word
     */
    List<WordContextTemplate> findByWordIdAndCreatedForUserId(UUID wordId, UUID createdForUserId);

    /**
     * Find a context by ID with word eagerly loaded
     */
    @Query("SELECT wct FROM WordContextTemplate wct " +
            "LEFT JOIN FETCH wct.word " +
            "WHERE wct.id = :id")
    Optional<WordContextTemplate> findByIdWithWord(@Param("id") UUID id);

    /**
     * Find contexts by source type
     */
    List<WordContextTemplate> findBySourceType(WordContextTemplate.SourceType sourceType);

    /**
     * Check if a context exists for a word (to avoid duplicates)
     */
    boolean existsByWordIdAndContextSentence(UUID wordId, String contextSentence);

    /**
     * Find all contexts created for a specific user
     */
    List<WordContextTemplate> findByCreatedForUserId(UUID userId);

    /**
     * Count contexts for a word
     */
    long countByWordId(UUID wordId);

    /**
     * Find all word contexts for a specific unit (shared contexts only)
     */
    @Query("SELECT wct FROM WordContextTemplate wct " +
            "LEFT JOIN FETCH wct.word " +
            "WHERE wct.unit.id = :unitId " +
            "AND wct.createdForUserId IS NULL " +
            "ORDER BY wct.word.lemma ASC")
    List<WordContextTemplate> findByUnitIdShared(@Param("unitId") UUID unitId);

    /**
     * Find all word contexts for a unit (shared + user-specific)
     */
    @Query("SELECT wct FROM WordContextTemplate wct " +
            "LEFT JOIN FETCH wct.word " +
            "WHERE wct.unit.id = :unitId " +
            "AND (wct.createdForUserId IS NULL OR wct.createdForUserId = :userId) " +
            "ORDER BY wct.word.lemma ASC")
    List<WordContextTemplate> findByUnitIdForUser(
            @Param("unitId") UUID unitId,
            @Param("userId") UUID userId);
}
