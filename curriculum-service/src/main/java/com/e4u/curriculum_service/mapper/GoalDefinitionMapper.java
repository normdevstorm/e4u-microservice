package com.e4u.curriculum_service.mapper;

import com.e4u.curriculum_service.entities.GoalDefinition;
import com.e4u.curriculum_service.models.request.GoalDefinitionCreateRequest;
import com.e4u.curriculum_service.models.request.GoalDefinitionUpdateRequest;
import com.e4u.curriculum_service.models.response.GoalDefinitionResponse;
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
