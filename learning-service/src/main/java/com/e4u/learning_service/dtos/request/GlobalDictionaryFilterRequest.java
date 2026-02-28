package com.e4u.learning_service.dtos.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for filtering GlobalDictionary entries.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GlobalDictionaryFilterRequest {

    private String lemma;
    private String partOfSpeech;
    private String difficultyLevel;
    private Float minFrequencyScore;
    private Float maxFrequencyScore;
    private String keyword;

    // Pagination
    @Builder.Default
    private Integer page = 0;
    @Builder.Default
    private Integer size = 20;
    @Builder.Default
    private String sortBy = "lemma";
    @Builder.Default
    private String sortDirection = "ASC";
}
