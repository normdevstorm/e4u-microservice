package com.e4u.learning_service.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

/**
 * Entity representing a translation of a word to a specific language.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "translation_dict", uniqueConstraints = @UniqueConstraint(columnNames = { "word_id", "dest_lang" }))
public class TranslationDict extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "word_id", nullable = false)
    private GlobalDictionary word;

    @Column(name = "dest_lang", nullable = false, length = 10)
    private String destLang;

    @Column(name = "trans", nullable = false, columnDefinition = "TEXT")
    private String translation;

    @Column(name = "example_translation", columnDefinition = "TEXT")
    private String exampleTranslation;
}
