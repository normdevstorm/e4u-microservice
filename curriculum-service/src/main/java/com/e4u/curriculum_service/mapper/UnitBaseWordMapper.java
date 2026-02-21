package com.e4u.curriculum_service.mapper;

import com.e4u.curriculum_service.entities.UnitBaseWord;
import com.e4u.curriculum_service.models.response.UnitBaseWordResponse;
import org.mapstruct.*;

import java.util.List;

/**
 * MapStruct mapper for UnitBaseWord entity.
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UnitBaseWordMapper {

    @Mapping(target = "lemma", source = "word.lemma")
    @Mapping(target = "partOfSpeech", source = "word.partOfSpeech")
    @Mapping(target = "definition", source = "word.definition")
    UnitBaseWordResponse toResponse(UnitBaseWord entity);

    List<UnitBaseWordResponse> toResponseList(List<UnitBaseWord> entities);
}
