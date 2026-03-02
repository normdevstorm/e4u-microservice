package com.e4u.learning_service.mapper;

import com.e4u.learning_service.dtos.response.ExerciseTemplateResponse;
import com.e4u.learning_service.entities.ExerciseTemplate;
import com.e4u.learning_service.entities.LessonTemplate;
import com.e4u.learning_service.entities.WordContextTemplate;
import org.mapstruct.*;

import java.util.List;
import java.util.UUID;

/**
 * MapStruct mapper for converting between ExerciseTemplate entity and DTOs.
 */
@Mapper(componentModel = "spring", 
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, 
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ExerciseTemplateMapper {

    /**
     * Convert entity to response DTO
     */
    @Named("toResponse")
    @Mapping(target = "lessonTemplateId", expression = "java(mapLessonTemplateId(entity.getLessonTemplate()))")
    @Mapping(target = "wordContextTemplateId", expression = "java(mapWordContextTemplateId(entity.getWordContextTemplate()))")
    @Mapping(target = "wordLemma", expression = "java(mapWordLemma(entity.getWordContextTemplate()))")
    ExerciseTemplateResponse toResponse(ExerciseTemplate entity);

    /**
     * Convert entity to response DTO without exposing correct answer.
     * Note: The exerciseData is mapped as-is. If correctAnswer needs to be hidden,
     * it should be filtered by the service layer or a custom serializer.
     */
    @Named("toResponseWithoutAnswer")
    @Mapping(target = "lessonTemplateId", expression = "java(mapLessonTemplateId(entity.getLessonTemplate()))")
    @Mapping(target = "wordContextTemplateId", expression = "java(mapWordContextTemplateId(entity.getWordContextTemplate()))")
    @Mapping(target = "wordLemma", expression = "java(mapWordLemma(entity.getWordContextTemplate()))")
    ExerciseTemplateResponse toResponseWithoutAnswer(ExerciseTemplate entity);

    /**
     * Convert list of entities to list of response DTOs
     */
    @IterableMapping(qualifiedByName = "toResponse")
    List<ExerciseTemplateResponse> toResponseList(List<ExerciseTemplate> entities);

    /**
     * Convert list of entities to list of response DTOs without correct answers
     */
    @IterableMapping(qualifiedByName = "toResponseWithoutAnswer")
    List<ExerciseTemplateResponse> toResponseListWithoutAnswer(List<ExerciseTemplate> entities);

    /**
     * Helper method to safely extract LessonTemplate ID
     */
    default UUID mapLessonTemplateId(LessonTemplate lessonTemplate) {
        return lessonTemplate != null ? lessonTemplate.getId() : null;
    }

    /**
     * Helper method to safely extract WordContextTemplate ID
     */
    default UUID mapWordContextTemplateId(WordContextTemplate wordContextTemplate) {
        return wordContextTemplate != null ? wordContextTemplate.getId() : null;
    }

    /**
     * Helper method to safely extract word lemma from WordContextTemplate
     */
    default String mapWordLemma(WordContextTemplate wordContextTemplate) {
        return wordContextTemplate != null && wordContextTemplate.getWord() != null 
            ? wordContextTemplate.getWord().getLemma() 
            : null;
    }
}
