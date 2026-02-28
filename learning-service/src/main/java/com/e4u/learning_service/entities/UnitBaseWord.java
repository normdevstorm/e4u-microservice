package com.e4u.learning_service.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

/**
 * Entity representing the relationship between curriculum units and dictionary
 * words.
 * Maps which words belong to which curriculum units.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "units_base_words")
@IdClass(UnitBaseWordId.class)
public class UnitBaseWord extends BaseEntity {

    @Id
    @Column(name = "unit_id")
    private UUID unitId;

    @Id
    @Column(name = "word_id")
    private UUID wordId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unit_id", nullable = false, insertable = false, updatable = false)
    private CurriculumUnit unit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "word_id", nullable = false, insertable = false, updatable = false)
    private GlobalDictionary word;

    @Column(name = "sequence_order")
    private Integer sequenceOrder;

    /**
     * Helper method to set unit and sync unit_id
     */
    public void setUnit(CurriculumUnit unit) {
        this.unit = unit;
        if (unit != null) {
            this.unitId = unit.getId();
        }
    }

    /**
     * Helper method to set word and sync word_id
     */
    public void setWord(GlobalDictionary word) {
        this.word = word;
        if (word != null) {
            this.wordId = word.getId();
        }
    }
}
