package com.e4u.learning_service.mapper;

import com.e4u.learning_service.dtos.request.CurriculumUnitCreateRequest;
import com.e4u.learning_service.dtos.request.CurriculumUnitUpdateRequest;
import com.e4u.learning_service.dtos.response.CurriculumUnitDetailResponse;
import com.e4u.learning_service.dtos.response.CurriculumUnitResponse;
import com.e4u.learning_service.entities.CurriculumUnit;

import org.mapstruct.*;

import java.util.List;

/**
 * MapStruct mapper for CurriculumUnit entity.
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {
                GlobalDictionaryMapper.class })
public interface CurriculumUnitMapper {

        @Mapping(target = "curriculumId", source = "curriculum.id")
        @Mapping(target = "curriculumName", source = "curriculum.curriculumName")
        @Mapping(target = "wordCount", expression = "java(entity.getBaseWords() != null ? (long) entity.getBaseWords().stream().filter(w -> !w.isDeleted()).count() : 0L)")
        CurriculumUnitResponse toResponse(CurriculumUnit entity);

        List<CurriculumUnitResponse> toResponseList(List<CurriculumUnit> entities);

        @Mapping(target = "curriculumId", source = "curriculum.id")
        @Mapping(target = "curriculumName", source = "curriculum.curriculumName")
        @Mapping(target = "baseWords", expression = "java(entity.getBaseWords() != null ? entity.getBaseWords().stream().filter(w -> !w.isDeleted()).map(w -> globalDictionaryMapper.toResponse(w.getWord())).collect(java.util.stream.Collectors.toList()) : null)")
        CurriculumUnitDetailResponse toDetailResponse(CurriculumUnit entity,
                        @Context GlobalDictionaryMapper globalDictionaryMapper);

        @Mapping(target = "id", ignore = true)
        @Mapping(target = "curriculum", ignore = true)
        @Mapping(target = "baseWords", ignore = true)
        CurriculumUnit toEntity(CurriculumUnitCreateRequest request);

        @Mapping(target = "id", ignore = true)
        @Mapping(target = "curriculum", ignore = true)
        @Mapping(target = "baseWords", ignore = true)
        @Mapping(target = "createdAt", ignore = true)
        @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
        void partialUpdate(@MappingTarget CurriculumUnit entity, CurriculumUnitUpdateRequest request);
}
