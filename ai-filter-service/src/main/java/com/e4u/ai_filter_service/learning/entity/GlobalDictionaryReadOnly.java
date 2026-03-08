package com.e4u.ai_filter_service.learning.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

/**
 * Lightweight read-only projection of {@code global_dictionary} in
 * {@code e4u_learning}.
 *
 * <p>
 * <strong>DO NOT add cascade operations, relationships, or DDL annotations
 * here.</strong>
 * This entity is only used for reading word metadata within the AI relevance
 * batch pipeline ({@code GlobalDictionaryItemReader} row mapper).
 *
 * <p>
 * Managed by: {@code learningEntityManager} → {@code e4u_learning} DB.
 */
@Entity
@Table(name = "global_dictionary")
@Getter
@Setter
@NoArgsConstructor
public class GlobalDictionaryReadOnly {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "lemma", nullable = false, length = 100)
    private String lemma;

    @Column(name = "part_of_speech", length = 20)
    private String partOfSpeech;

    @Column(name = "definition", columnDefinition = "TEXT")
    private String definition;

    @Column(name = "example_sentence", columnDefinition = "TEXT")
    private String exampleSentence;

    @Column(name = "deleted", nullable = false)
    private boolean deleted = false;

    @Column(name = "created_at")
    private Instant createdAt;
}
