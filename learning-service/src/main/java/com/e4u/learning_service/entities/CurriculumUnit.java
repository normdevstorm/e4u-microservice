package com.e4u.learning_service.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Entity representing a unit within a curriculum.
 * Each unit contains vocabulary words and has a specific proficiency
 * requirement.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "curriculum_units")
public class CurriculumUnit extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "curriculum_id")
    private Curriculum curriculum;

    @Column(name = "unit_name", nullable = false, length = 100)
    private String unitName;

    @Column(name = "required_proficiency_level", length = 5)
    private String requiredProficiencyLevel;

    @Column(name = "default_order")
    private Integer defaultOrder;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "base_keywords", columnDefinition = "TEXT[]")
    @Builder.Default
    private List<String> baseKeywords = new ArrayList<>();

    @Column(name = "description")
    private String description;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @OneToMany(mappedBy = "unit", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<WordContextTemplate> wordContextTemplates = new HashSet<>();

    /**
     * Add a word context template to this unit
     */
    public void addWordContextTemplate(WordContextTemplate wordContextTemplate) {
        wordContextTemplates.add(wordContextTemplate);
        wordContextTemplate.setUnit(this);
    }

    /**
     * Remove a word context template from this unit
     */
    public void removeWordContextTemplate(WordContextTemplate wordContextTemplate) {
        wordContextTemplates.remove(wordContextTemplate);
        wordContextTemplate.setUnit(null);
    }
}
