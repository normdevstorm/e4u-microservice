package com.e4u.learning_service.mapper;

import com.e4u.learning_service.dtos.response.UserLessonSessionDetailResponse;
import com.e4u.learning_service.dtos.response.UserLessonSessionResponse;
import com.e4u.learning_service.entities.LessonTemplate;
import com.e4u.learning_service.entities.UserLessonSession;
import com.e4u.learning_service.entities.UserUnitState;
import org.mapstruct.*;

import java.util.List;
import java.util.UUID;

/**
 * MapStruct mapper for converting between UserLessonSession entity and DTOs.
 */
@Mapper(componentModel = "spring", 
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, 
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {ExerciseTemplateMapper.class, UserExerciseAttemptMapper.class})
public interface UserLessonSessionMapper {

    /**
     * Convert entity to response DTO
     */
    @Mapping(target = "lessonTemplateId", expression = "java(mapLessonTemplateId(entity.getLessonTemplate()))")
    @Mapping(target = "lessonName", expression = "java(mapLessonName(entity.getLessonTemplate()))")
    @Mapping(target = "userUnitStateId", expression = "java(mapUserUnitStateId(entity.getUserUnitState()))")
    UserLessonSessionResponse toResponse(UserLessonSession entity);

    /**
     * Convert list of entities to list of response DTOs
     */
    List<UserLessonSessionResponse> toResponseList(List<UserLessonSession> entities);

    /**
     * Convert entity to detailed response DTO
     */
    @Mapping(target = "lessonTemplateId", expression = "java(mapLessonTemplateId(entity.getLessonTemplate()))")
    @Mapping(target = "lessonName", expression = "java(mapLessonName(entity.getLessonTemplate()))")
    @Mapping(target = "userUnitStateId", expression = "java(mapUserUnitStateId(entity.getUserUnitState()))")
    @Mapping(target = "exercises", ignore = true) // Set manually with exercises without answers
    @Mapping(target = "attempts", source = "exerciseAttempts")
    @Mapping(target = "currentExerciseIndex", expression = "java(entity.getCompletedItems())")
    UserLessonSessionDetailResponse toDetailResponse(UserLessonSession entity);

    /**
     * Helper method to safely extract LessonTemplate ID
     */
    default UUID mapLessonTemplateId(LessonTemplate lessonTemplate) {
        return lessonTemplate != null ? lessonTemplate.getId() : null;
    }

    /**
     * Helper method to safely extract lesson name
     */
    default String mapLessonName(LessonTemplate lessonTemplate) {
        return lessonTemplate != null ? lessonTemplate.getLessonName() : null;
    }

    /**
     * Helper method to safely extract UserUnitState ID
     */
    default UUID mapUserUnitStateId(UserUnitState userUnitState) {
        return userUnitState != null ? userUnitState.getId() : null;
    }
}
