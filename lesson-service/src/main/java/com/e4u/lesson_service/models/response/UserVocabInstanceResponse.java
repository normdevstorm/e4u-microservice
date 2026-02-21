package com.e4u.lesson_service.models.response;

import com.e4u.lesson_service.entities.UserVocabInstance.SourceType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

/**
 * Response model for UserVocabInstance.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserVocabInstanceResponse {

    private UUID id;

    private UUID userId;

    private UUID wordLemmaId;

    private String wordText;

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

    private Boolean isMastered;

    private String media;

    private String contextUrl;

    private SourceType sourceType;

    private Instant createdAt;

    private Instant updatedAt;
}
