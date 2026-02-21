package com.e4u.curriculum_service.mapper;

import com.e4u.curriculum_service.entities.GlobalDictionary;
import com.e4u.curriculum_service.models.request.GlobalDictionaryCreateRequest;
import com.e4u.curriculum_service.models.request.GlobalDictionaryUpdateRequest;
import com.e4u.curriculum_service.models.response.GlobalDictionaryResponse;
import org.mapstruct.*;

import java.util.List;

/**
 * MapStruct mapper for GlobalDictionary entity.
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {
        TranslationDictMapper.class })
public interface GlobalDictionaryMapper {

    @Named("toResponse")
    @Mapping(target = "translations", ignore = true)
    GlobalDictionaryResponse toResponse(GlobalDictionary entity);

    @Named("toResponseWithTranslations")
    @Mapping(target = "translations", source = "translations")
    GlobalDictionaryResponse toResponseWithTranslations(GlobalDictionary entity);

    @IterableMapping(qualifiedByName = "toResponse")
    List<GlobalDictionaryResponse> toResponseList(List<GlobalDictionary> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "translations", ignore = true)
    @Mapping(target = "unitBaseWords", ignore = true)
    GlobalDictionary toEntity(GlobalDictionaryCreateRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "translations", ignore = true)
    @Mapping(target = "unitBaseWords", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void partialUpdate(@MappingTarget GlobalDictionary entity, GlobalDictionaryUpdateRequest request);
}
