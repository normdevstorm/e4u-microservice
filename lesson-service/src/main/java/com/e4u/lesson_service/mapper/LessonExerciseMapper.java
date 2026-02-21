package com.e4u.lesson_service.mapper;

import com.e4u.lesson_service.entities.DynamicLesson;
import com.e4u.lesson_service.entities.LessonExercise;
import com.e4u.lesson_service.entities.UserVocabInstance;
import com.e4u.lesson_service.models.request.LessonExerciseCreateRequest;
import com.e4u.lesson_service.models.request.LessonExerciseUpdateRequest;
import com.e4u.lesson_service.models.response.LessonExerciseDetailResponse;
import com.e4u.lesson_service.models.response.LessonExerciseResponse;
import org.mapstruct.*;

import java.util.List;
import java.util.UUID;

/**
 * MapStruct mapper for converting between LessonExercise entity and DTOs.
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {
        UserVocabInstanceMapper.class })
public interface LessonExerciseMapper {

    /**
     * Convert entity to response DTO
     */
    @Mapping(target = "lessonId", expression = "java(mapLessonId(entity.getLesson()))")
    @Mapping(target = "wordInstanceId", expression = "java(mapWordInstanceId(entity.getWordInstance()))")
    LessonExerciseResponse toResponse(LessonExercise entity);

    /**
     * Convert list of entities to list of response DTOs
     */
    List<LessonExerciseResponse> toResponseList(List<LessonExercise> entities);

    /**
     * Convert entity to detailed response DTO
     */
    @Mapping(target = "lessonId", expression = "java(mapLessonId(entity.getLesson()))")
    @Mapping(target = "wordInstance", source = "wordInstance")
    LessonExerciseDetailResponse toDetailResponse(LessonExercise entity);

    /**
     * Convert list of entities to list of detailed response DTOs
     */
    List<LessonExerciseDetailResponse> toDetailResponseList(List<LessonExercise> entities);

    /**
     * Convert create request to entity (without relationships)
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "lesson", ignore = true)
    @Mapping(target = "wordInstance", ignore = true)
    @Mapping(target = "isCompleted", constant = "false")
    @Mapping(target = "isCorrect", ignore = true)
    @Mapping(target = "userAnswer", ignore = true)
    @Mapping(target = "timeSpentSeconds", ignore = true)
    LessonExercise toEntity(LessonExerciseCreateRequest request);

    /**
     * Partially update entity from update request
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "lesson", ignore = true)
    @Mapping(target = "wordInstance", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    void partialUpdate(@MappingTarget LessonExercise entity, LessonExerciseUpdateRequest request);

    /**
     * Helper method to safely extract Lesson ID
     */
    default UUID mapLessonId(DynamicLesson lesson) {
        return lesson != null ? lesson.getId() : null;
    }

    /**
     * Helper method to safely extract WordInstance ID
     */
    default UUID mapWordInstanceId(UserVocabInstance wordInstance) {
        return wordInstance != null ? wordInstance.getId() : null;
    }
}
