package com.e4u.learning_service.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Entity representing a word in the global dictionary.
 * Contains the base form (lemma), part of speech, and other linguistic
 * metadata.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "global_dictionary")
public class GlobalDictionary extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(name = "lemma", nullable = false, length = 100)
    private String lemma;

    @Column(name = "part_of_speech", length = 20)
    private String partOfSpeech;

    @Column(name = "definition", columnDefinition = "TEXT")
    private String definition;

    @Column(name = "difficulty_level", length = 5)
    private String difficultyLevel;

    @Column(name = "frequency_score")
    private Float frequencyScore;

    @Column(name = "phonetic")
    private String phonetic;

    @Column(name = "audio_url")
    private String audioUrl;

    @Column(name = "example_sentence", columnDefinition = "TEXT")
    private String exampleSentence;

    @OneToMany(mappedBy = "word", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<TranslationDict> translations = new HashSet<>();

    @OneToMany(mappedBy = "word", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<UnitBaseWord> unitBaseWords = new HashSet<>();

    /**
     * Add a translation to this word
     */
    public void addTranslation(TranslationDict translation) {
        translations.add(translation);
        translation.setWord(this);
    }

    /**
     * Remove a translation from this word
     */
    public void removeTranslation(TranslationDict translation) {
        translations.remove(translation);
        translation.setWord(null);
    }
}
