package com.e4u.lesson_service.models.request;

import com.e4u.lesson_service.entities.UserVocabInstance.SourceType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Request model for filtering UserVocabInstances.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserVocabInstanceFilterRequest {

    private UUID userId;

    private String wordText;

    private Boolean isJobMatch;

    private Boolean isGoalMatch;

    private Boolean isLearning;

    private Boolean isMastered;

    private SourceType sourceType;

    private Float minRelevanceScore;

    private Float maxRelevanceScore;

    private Integer minFrequencyCount;

    private Integer maxFrequencyCount;

    // Pagination
    @Builder.Default
    private Integer page = 0;

    @Builder.Default
    private Integer size = 20;

    // Sorting
    @Builder.Default
    private String sortBy = "createdAt";

    @Builder.Default
    private String sortDirection = "DESC";
}
