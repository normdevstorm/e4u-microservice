package com.renting.item_service.mapper.item;

import com.renting.item_service.dto.item.ItemRequestDto;
import com.renting.item_service.dto.item.ItemResponseDto;
import com.renting.item_service.entity.Item;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface ItemMapper {
    @Mapping(target = "createdAt", expression = "java(itemRequestDto.getCreatedAt() != null ? itemRequestDto.getCreatedAt() : LocalDateTime.now())")
    Item toEntity(ItemRequestDto itemRequestDto);

    @Mapping(target = "ownerId", source = "ownerId")
    ItemResponseDto toDto(Item item);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Item partialUpdate(ItemRequestDto itemRequestDto, @MappingTarget Item item);
}