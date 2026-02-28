package com.e4u.learning_service.dtos.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Request DTO for creating a GlobalDictionary entry.
 * Supports embedding translations in the same request.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GlobalDictionaryCreateRequest {

    @NotBlank(message = "Lemma is required")
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

    /**
     * Optional list of translations to create along with the dictionary entry.
     * Each translation will be associated with the newly created word.
     */
    @Valid
    @Builder.Default
    private List<TranslationEmbeddedRequest> translations = new ArrayList<>();
}
