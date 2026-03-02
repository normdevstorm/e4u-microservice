package com.e4u.learning_service.mapper;

import com.e4u.learning_service.dtos.response.LessonTemplateDetailResponse;
import com.e4u.learning_service.dtos.response.LessonTemplateResponse;
import com.e4u.learning_service.entities.CurriculumUnit;
import com.e4u.learning_service.entities.LessonTemplate;
import org.mapstruct.*;

import java.util.List;
import java.util.UUID;

/**
 * MapStruct mapper for converting between LessonTemplate entity and DTOs.
 */
@Mapper(componentModel = "spring", 
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, 
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {ExerciseTemplateMapper.class})
public interface LessonTemplateMapper {

    /**
     * Convert entity to response DTO
     */
    @Mapping(target = "unitId", expression = "java(mapUnitId(entity.getUnit()))")
    @Mapping(target = "exerciseCount", expression = "java(entity.getExerciseTemplates() != null ? entity.getExerciseTemplates().size() : 0)")
    LessonTemplateResponse toResponse(LessonTemplate entity);

    /**
     * Convert list of entities to list of response DTOs
     */
    List<LessonTemplateResponse> toResponseList(List<LessonTemplate> entities);

    /**
     * Convert entity to detailed response DTO with exercises
     */
    @Mapping(target = "unitId", expression = "java(mapUnitId(entity.getUnit()))")
    @Mapping(target = "unitName", expression = "java(mapUnitName(entity.getUnit()))")
    @Mapping(target = "exercises", ignore = true) // Set manually in service to control answer visibility
    LessonTemplateDetailResponse toDetailResponse(LessonTemplate entity);

    /**
     * Helper method to safely extract Unit ID
     */
    default UUID mapUnitId(CurriculumUnit unit) {
        return unit != null ? unit.getId() : null;
    }

    /**
     * Helper method to safely extract Unit name
     */
    default String mapUnitName(CurriculumUnit unit) {
        return unit != null ? unit.getUnitName() : null;
    }
}
