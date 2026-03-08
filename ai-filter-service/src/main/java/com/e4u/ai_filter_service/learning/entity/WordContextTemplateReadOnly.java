package com.e4u.ai_filter_service.learning.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Immutable;

import java.util.UUID;

/**
 * Read-only JPA projection of {@code e4u_learning.word_context_templates}.
 *
 * <p>
 * Only the columns relevant to the AI filter batch pipeline are mapped here.
 * This entity is <strong>not</strong> meant for general-purpose use — it is
 * scoped to the {@code learningEntityManager} (secondary DataSource) and
 * should only be accessed via {@link WordContextTemplateReadRepository}.
 *
 * <p>
 * Key semantics:
 * <ul>
 * <li>{@code createdForUserId IS NOT NULL} → user-specific template (batch
 * input)</li>
 * <li>{@code aiReasoning IS NULL} → not yet evaluated by the AI batch job</li>
 * <li>{@code isSelectedByAi = true} → word is active in the curriculum
 * unit</li>
 * </ul>
 *
 * <p>
 * The {@code @Immutable} annotation prevents Hibernate from tracking dirty
 * state,
 * which is correct because all write-backs go through native {@code @Modifying}
 * queries in the repository — never via entity flush.
 */
@Entity
@Immutable
@Table(name = "word_context_templates")
@Getter
@Setter
@NoArgsConstructor
public class WordContextTemplateReadOnly {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    /**
     * FK to {@code curriculum_units.id}.
     * Determines which unit this word belongs to — used only for context;
     * the actual curriculum insertion is driven by {@code isSelectedByAi}.
     */
    @Column(name = "unit_id", nullable = false)
    private UUID unitId;

    /**
     * FK to {@code global_dictionary.id}.
     * The vocabulary word being evaluated.
     */
    @Column(name = "word_id", nullable = false)
    private UUID wordId;

    /**
     * The user this context template was generated for.
     * {@code NULL} = system/shared template (not a batch input target).
     * {@code NOT NULL} = user-specific context ready for AI evaluation.
     */
    @Column(name = "created_for_user_id")
    private UUID createdForUserId;

    /**
     * Whether the AI batch job selected this word for inclusion in the curriculum.
     * <ul>
     * <li>{@code false} (default) = pending or rejected</li>
     * <li>{@code true} = AI approved; word is active in the curriculum unit</li>
     * </ul>
     */
    @Column(name = "is_selected_by_ai", nullable = false)
    private Boolean isSelectedByAi;

    /**
     * User-specific example sentence for this word in its curriculum unit context.
     * Passed to the AI prompt to give richer relevance context than the generic
     * {@code global_dictionary.example_sentence}.
     */
    @Column(name = "context_sentence", columnDefinition = "TEXT")
    private String contextSentence;

    /**
     * AI reasoning populated after batch processing.
     * {@code NULL} = not yet evaluated.
     * {@code NOT NULL} = already processed (skipped by the reader in next run).
     */
    @Column(name = "ai_reasoning", columnDefinition = "TEXT")
    private String aiReasoning;

    /** Source type — SYSTEM, AI_GENERATED, or USER_EXTENSION. */
    @Column(name = "source_type", length = 30)
    private String sourceType;
}
