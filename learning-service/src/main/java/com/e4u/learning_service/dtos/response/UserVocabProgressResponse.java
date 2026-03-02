package com.e4u.learning_service.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Response model for UserVocabProgress.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserVocabProgressResponse {

    private UUID id;

    private UUID userId;

    private UUID wordId;

    private String word;

    private String wordMeaning;

    private UUID activeContextId;

    private String activeContextSentence;

    private Boolean isMastered;

    private Float relevanceScore;

    // SRS fields
    private Integer intervalDays;

    private Float easeFactor;

    private Integer consecutiveCorrectAnswers;

    private LocalDateTime nextReviewAt;

    private Instant createdAt;

    private Instant updatedAt;
}
