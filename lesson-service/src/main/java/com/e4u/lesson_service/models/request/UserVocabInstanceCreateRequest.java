package com.e4u.lesson_service.models.request;

import com.e4u.lesson_service.entities.UserVocabInstance.SourceType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Request model for creating a new UserVocabInstance.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserVocabInstanceCreateRequest {

    @NotNull(message = "User ID is required")
    private UUID userId;

    private UUID wordLemmaId;

    @NotBlank(message = "Word text is required")
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

    @Builder.Default
    private Boolean isLearning = true;

    @Builder.Default
    private Boolean isMastered = false;

    private String media;

    private String contextUrl;

    private SourceType sourceType;
}
