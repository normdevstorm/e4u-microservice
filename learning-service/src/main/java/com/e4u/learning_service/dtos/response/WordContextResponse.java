package com.e4u.learning_service.dtos.response;

import com.e4u.learning_service.entities.WordContextTemplate.SourceType;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

/**
 * Response DTO for WordContextTemplate.
 * Represents a word with its contextual information within a unit.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WordContextResponse {

    private UUID id;
    private UUID unitId;

    // Word info from GlobalDictionary
    private UUID wordId;
    private String word;
    private String pronunciation;
    private String partOfSpeech;
    private String meaning;

    // Context info
    private String specificMeaning;
    private String contextSentence;
    private String contextTranslation;
    private SourceType sourceType;

    // Whether this is a user-specific context
    private UUID createdForUserId;

    private Instant createdAt;
    private Instant updatedAt;
}
