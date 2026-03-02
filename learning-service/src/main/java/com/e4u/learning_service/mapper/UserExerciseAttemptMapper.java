package com.e4u.learning_service.mapper;

import com.e4u.learning_service.dtos.response.UserExerciseAttemptResponse;
import com.e4u.learning_service.entities.ExerciseTemplate;
import com.e4u.learning_service.entities.UserExerciseAttempt;
import com.e4u.learning_service.entities.UserLessonSession;
import com.e4u.learning_service.entities.pojos.ExerciseData;
import org.mapstruct.*;

import java.util.List;
import java.util.UUID;

/**
 * MapStruct mapper for converting between UserExerciseAttempt entity and DTOs.
 */
@Mapper(componentModel = "spring", 
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, 
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserExerciseAttemptMapper {

    /**
     * Convert entity to response DTO (without correct answer)
     */
    @Named("toResponse")
    @Mapping(target = "sessionId", expression = "java(mapSessionId(entity.getSession()))")
    @Mapping(target = "exerciseTemplateId", expression = "java(mapExerciseTemplateId(entity.getExerciseTemplate()))")
    @Mapping(target = "exerciseType", expression = "java(entity.getExerciseType())")
    @Mapping(target = "exerciseData", ignore = true)
    @Mapping(target = "feedback", ignore = true)
    UserExerciseAttemptResponse toResponse(UserExerciseAttempt entity);

    /**
     * Convert entity to response DTO with correct answer revealed
     */
    @Named("toResponseWithAnswer")
    @Mapping(target = "sessionId", expression = "java(mapSessionId(entity.getSession()))")
    @Mapping(target = "exerciseTemplateId", expression = "java(mapExerciseTemplateId(entity.getExerciseTemplate()))")
    @Mapping(target = "exerciseType", expression = "java(entity.getExerciseType())")
    @Mapping(target = "exerciseData", expression = "java(mapExerciseData(entity.getExerciseTemplate()))")
    @Mapping(target = "feedback", ignore = true)
    UserExerciseAttemptResponse toResponseWithAnswer(UserExerciseAttempt entity);

    /**
     * Convert list of entities to list of response DTOs
     */
    @IterableMapping(qualifiedByName = "toResponse")
    List<UserExerciseAttemptResponse> toResponseList(List<UserExerciseAttempt> entities);

    /**
     * Helper method to safely extract Session ID
     */
    default UUID mapSessionId(UserLessonSession session) {
        return session != null ? session.getId() : null;
    }

    /**
     * Helper method to safely extract ExerciseTemplate ID
     */
    default UUID mapExerciseTemplateId(ExerciseTemplate template) {
        return template != null ? template.getId() : null;
    }

    /**
     * Helper method to safely extract ExerciseData
     */
    default ExerciseData mapExerciseData(ExerciseTemplate template) {
        return template != null ? template.getExerciseData() : null;
    }
}
