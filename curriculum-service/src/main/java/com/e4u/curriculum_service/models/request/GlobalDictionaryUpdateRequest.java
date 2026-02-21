package com.e4u.curriculum_service.models.request;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for partially updating a GlobalDictionary entry.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GlobalDictionaryUpdateRequest {

    @Size(max = 100, message = "Lemma must not exceed 100 characters")
    private String lemma;

    @Size(max = 20, message = "Part of speech must not exceed 20 characters")
    private String partOfSpeech;

    private String definition;

    @Size(max = 5, message = "Difficulty level must not exceed 5 characters")
    private String difficultyLevel;

    private Float frequencyScore;

    private String phonetic;

    private String audioUrl;

    private String exampleSentence;
}
