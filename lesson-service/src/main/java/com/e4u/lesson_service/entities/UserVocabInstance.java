package com.e4u.lesson_service.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;
import java.util.UUID;

@Getter @Setter
@Entity
public class UserVocabInstance extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private UUID userId;
    private UUID wordLemmaId;
    private String wordText;
    /// TODO: COME UP WTIH SOLUTIONS FOR MULTI-TRANSLATION SUPPORT
    private String originalSentence;
    private String contextSentence;
    private String contextSpecificWordMeaning;
    private String contextSentenceTranslation;
    private Boolean isJobMatch;
    private Float weightOfInterest;
    private Boolean isGoalMatch;
    private Float weightOfGoal;
    private String aiReasoning;
    private Integer frequencyCount;
    private Float relevanceScore;
    private Boolean isLearning;
    private Boolean isMastered = Boolean.FALSE;
    private String media;
    private String contextUrl;
    @Enumerated(EnumType.STRING)
    private SourceType sourceType; // EXTENSION | AI_SUGGESTION
    public enum SourceType {
        EXTENSION,
        AI_SUGGESTION
    }
    @OneToMany(mappedBy = "wordInstance", fetch = FetchType.LAZY)
    private Set<LessonExercise> exercises;

    @OneToMany(mappedBy = "wordInstance", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<VocabAsset> assets;
}
