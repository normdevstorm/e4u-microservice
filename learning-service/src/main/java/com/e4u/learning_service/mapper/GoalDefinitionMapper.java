package com.e4u.learning_service.mapper;

import com.e4u.learning_service.dtos.request.GoalDefinitionCreateRequest;
import com.e4u.learning_service.dtos.request.GoalDefinitionUpdateRequest;
import com.e4u.learning_service.dtos.response.GoalDefinitionResponse;
import com.e4u.learning_service.entities.GoalDefinition;

import org.mapstruct.*;

import java.util.List;

/**
 * MapStruct mapper for GoalDefinition entity.
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface GoalDefinitionMapper {

    GoalDefinitionResponse toResponse(GoalDefinition entity);

    List<GoalDefinitionResponse> toResponseList(List<GoalDefinition> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userGoals", ignore = true)
    @Mapping(target = "curricula", ignore = true)
    GoalDefinition toEntity(GoalDefinitionCreateRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userGoals", ignore = true)
    @Mapping(target = "curricula", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void partialUpdate(@MappingTarget GoalDefinition entity, GoalDefinitionUpdateRequest request);
}
