package com.e4u.learning_service.mapper;

import com.e4u.learning_service.dtos.response.UnitBaseWordResponse;
import com.e4u.learning_service.entities.UnitBaseWord;

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
