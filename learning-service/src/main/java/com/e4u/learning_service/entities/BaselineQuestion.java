package com.e4u.learning_service.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Standalone MCQ question used in the F-02 English proficiency placement test.
 * Deliberately separate from ExerciseTemplate to keep baseline data simple and
 * independently queryable.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "baseline_questions")
public class BaselineQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    /** CEFR tier this question targets: A1 | A2 | B1 | B2 | C1 */
    @Column(name = "cefr_tier", nullable = false, length = 2)
    private String cefrTier;

    /** The question stem / fill-in-the-blank sentence shown to the user */
    @Column(name = "prompt", nullable = false, columnDefinition = "text")
    private String prompt;

    /** 4-choice option list stored as a Postgres text[] array */
    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "options", nullable = false, columnDefinition = "text[]")
    private List<String> options;

    /**
     * Correct answer — one of the strings in {@code options}. NOT exposed to
     * client.
     */
    @Column(name = "correct_answer", nullable = false, columnDefinition = "text")
    private String correctAnswer;

    /** Ordering hint within a tier group */
    @Builder.Default
    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder = 0;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @PrePersist
    private void onCreate() {
        createdAt = OffsetDateTime.now();
        updatedAt = OffsetDateTime.now();
    }

    @PreUpdate
    private void onUpdate() {
        updatedAt = OffsetDateTime.now();
    }
}
