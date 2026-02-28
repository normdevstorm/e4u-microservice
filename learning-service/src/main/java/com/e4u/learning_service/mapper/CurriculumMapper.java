package com.e4u.learning_service.mapper;

import com.e4u.learning_service.dtos.request.CurriculumCreateRequest;
import com.e4u.learning_service.dtos.request.CurriculumUpdateRequest;
import com.e4u.learning_service.dtos.response.CurriculumDetailResponse;
import com.e4u.learning_service.dtos.response.CurriculumResponse;
import com.e4u.learning_service.entities.Curriculum;

import org.mapstruct.*;

import java.util.List;

/**
 * MapStruct mapper for Curriculum entity.
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {
        CurriculumUnitMapper.class })
public interface CurriculumMapper {

    @Mapping(target = "goalId", source = "goalDefinition.id")
    @Mapping(target = "goalName", source = "goalDefinition.goalName")
    @Mapping(target = "unitCount", expression = "java(entity.getUnits() != null ? (int) entity.getUnits().stream().filter(u -> !u.isDeleted()).count() : 0)")
    CurriculumResponse toResponse(Curriculum entity);

    List<CurriculumResponse> toResponseList(List<Curriculum> entities);

    @Mapping(target = "goalId", source = "goalDefinition.id")
    @Mapping(target = "goalName", source = "goalDefinition.goalName")
    @Mapping(target = "units", source = "units")
    CurriculumDetailResponse toDetailResponse(Curriculum entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "goalDefinition", ignore = true)
    @Mapping(target = "units", ignore = true)
    Curriculum toEntity(CurriculumCreateRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "goalDefinition", ignore = true)
    @Mapping(target = "units", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void partialUpdate(@MappingTarget Curriculum entity, CurriculumUpdateRequest request);
}
