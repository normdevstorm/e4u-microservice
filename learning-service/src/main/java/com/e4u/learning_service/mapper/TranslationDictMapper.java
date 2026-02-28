package com.e4u.learning_service.mapper;

import com.e4u.learning_service.dtos.request.TranslationDictCreateRequest;
import com.e4u.learning_service.dtos.request.TranslationDictUpdateRequest;
import com.e4u.learning_service.dtos.response.TranslationDictResponse;
import com.e4u.learning_service.entities.TranslationDict;

import org.mapstruct.*;

import java.util.List;

/**
 * MapStruct mapper for TranslationDict entity.
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TranslationDictMapper {

    @Mapping(target = "wordId", source = "word.id")
    @Mapping(target = "lemma", source = "word.lemma")
    TranslationDictResponse toResponse(TranslationDict entity);

    List<TranslationDictResponse> toResponseList(List<TranslationDict> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "word", ignore = true)
    TranslationDict toEntity(TranslationDictCreateRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "word", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void partialUpdate(@MappingTarget TranslationDict entity, TranslationDictUpdateRequest request);
}
