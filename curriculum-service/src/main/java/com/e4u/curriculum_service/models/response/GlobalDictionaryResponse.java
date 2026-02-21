package com.e4u.curriculum_service.models.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Response DTO for GlobalDictionary.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GlobalDictionaryResponse {

    private UUID id;
    private String lemma;
    private String partOfSpeech;
    private String definition;
    private String difficultyLevel;
    private Float frequencyScore;
    private String phonetic;
    private String audioUrl;
    private String exampleSentence;
    private Instant createdAt;
    private Instant updatedAt;

    // Optional: Include translations
    private List<TranslationDictResponse> translations;
}
