package com.e4u.learning_service.mapper;

import com.e4u.learning_service.dtos.request.UserGoalCreateRequest;
import com.e4u.learning_service.dtos.request.UserGoalUpdateRequest;
import com.e4u.learning_service.dtos.response.UserGoalResponse;
import com.e4u.learning_service.entities.UserGoal;

import org.mapstruct.*;

import java.util.List;

/**
 * MapStruct mapper for UserGoal entity.
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {
        GoalDefinitionMapper.class })
public interface UserGoalMapper {

    @Named("toResponse")
    @Mapping(target = "goalName", source = "goalDefinition.goalName")
    @Mapping(target = "goal", source = "goalDefinition")
    UserGoalResponse toResponse(UserGoal entity);

    @Named("toSimpleResponse")
    @Mapping(target = "goalName", source = "goalDefinition.goalName")
    @Mapping(target = "goal", ignore = true)
    UserGoalResponse toSimpleResponse(UserGoal entity);

    @IterableMapping(qualifiedByName = "toResponse")
    List<UserGoalResponse> toResponseList(List<UserGoal> entities);

    @Mapping(target = "goalDefinition", ignore = true)
    @Mapping(target = "startedAt", ignore = true)
    UserGoal toEntity(UserGoalCreateRequest request);

    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "goalId", ignore = true)
    @Mapping(target = "goalDefinition", ignore = true)
    @Mapping(target = "startedAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void partialUpdate(@MappingTarget UserGoal entity, UserGoalUpdateRequest request);
}
