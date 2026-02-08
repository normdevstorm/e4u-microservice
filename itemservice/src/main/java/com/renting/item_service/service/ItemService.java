package com.renting.item_service.service;

import com.renting.item_service.common.exception.custom.CustomEntityNotFoundException;
import com.renting.item_service.dto.item.ItemRequestDto;
import com.renting.item_service.dto.item.ItemResponseDto;
import com.renting.item_service.entity.Item;
import com.renting.item_service.mapper.item.ItemMapper;
import com.renting.item_service.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ItemService {
    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;
    private final SecurityContextService securityContextService;
    // Add a service to get the userId as ownerId from the JWT token => call Feign authentication service
//    private final JwtService jwtService;

    @Autowired
    public ItemService(ItemRepository itemRepository, ItemMapper itemMapper, SecurityContextService securityContextService
    ) {
        this.itemRepository = itemRepository;
        this.itemMapper = itemMapper;
        this.securityContextService = securityContextService;
    }

    public Item findById(UUID itemId) {
        return (Item) itemRepository.findItemById((itemId)).orElseThrow(() -> new CustomEntityNotFoundException("Item not found with id: " + itemId));
    }

    @Transactional
    public ItemResponseDto addItem(ItemRequestDto itemRequestDto) {
        Item newItem = itemMapper.toEntity(itemRequestDto);
        UUID ownerId = securityContextService.getCurrentUser().getId();
        newItem.setOwnerId(ownerId);
        return itemMapper.toDto(itemRepository.save(newItem));
    }

    public List<ItemResponseDto> getAllItems() {
        // get all items of the user who executed the request only
        UUID currentUserId = securityContextService.getCurrentUser().getId();
        List<Item> items = itemRepository.findAllByOwnerId(currentUserId);
        return items.stream().map(itemMapper::toDto).collect(Collectors.toList());
    }

    public ItemResponseDto getItemById(String itemId) {
//        User user = jwtService.getUserFromContext();
        Item item = findById(UUID.fromString(itemId));
        return itemMapper.toDto(item);
    }

    public void deleteItem(String itemId) {
        Item item = findById(UUID.fromString(itemId));
        if (item != null) {
            itemRepository.delete(item);
        } else {
            throw new CustomEntityNotFoundException("Item not found with id: " + itemId);
        }
    }
}
