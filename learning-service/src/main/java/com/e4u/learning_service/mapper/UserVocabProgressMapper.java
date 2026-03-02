package com.e4u.learning_service.mapper;

import com.e4u.learning_service.dtos.response.UserVocabProgressResponse;
import com.e4u.learning_service.entities.GlobalDictionary;
import com.e4u.learning_service.entities.UserVocabProgress;
import com.e4u.learning_service.entities.WordContextTemplate;
import org.mapstruct.*;

import java.util.List;
import java.util.UUID;

/**
 * MapStruct mapper for converting between UserVocabProgress entity and DTOs.
 */
@Mapper(componentModel = "spring", 
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, 
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserVocabProgressMapper {

    /**
     * Convert entity to response DTO
     */
    @Mapping(target = "wordId", expression = "java(mapWordId(entity.getWord()))")
    @Mapping(target = "word", expression = "java(mapWordString(entity.getWord()))")
    @Mapping(target = "wordMeaning", expression = "java(mapWordMeaning(entity.getWord()))")
    @Mapping(target = "activeContextId", expression = "java(mapContextId(entity.getActiveContext()))")
    @Mapping(target = "activeContextSentence", expression = "java(mapContextSentence(entity.getActiveContext()))")
    UserVocabProgressResponse toResponse(UserVocabProgress entity);

    /**
     * Convert list of entities to list of response DTOs
     */
    List<UserVocabProgressResponse> toResponseList(List<UserVocabProgress> entities);

    /**
     * Helper method to safely extract Word ID
     */
    default UUID mapWordId(GlobalDictionary word) {
        return word != null ? word.getId() : null;
    }

    /**
     * Helper method to safely extract word string (lemma)
     */
    default String mapWordString(GlobalDictionary word) {
        return word != null ? word.getLemma() : null;
    }

    /**
     * Helper method to safely extract word meaning (definition)
     */
    default String mapWordMeaning(GlobalDictionary word) {
        return word != null ? word.getDefinition() : null;
    }

    /**
     * Helper method to safely extract context ID
     */
    default UUID mapContextId(WordContextTemplate context) {
        return context != null ? context.getId() : null;
    }

    /**
     * Helper method to safely extract context sentence
     */
    default String mapContextSentence(WordContextTemplate context) {
        return context != null ? context.getContextSentence() : null;
    }
}
