package com.renting.item_service.common.advice;

import com.renting.item_service.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component("postSecurity")
public class PostSecurity {

    @Autowired
    private ItemService itemService;

    public boolean isItemOwner(UUID itemId, UUID currentUserId) {
        return itemService.findById(itemId).getOwnerId().equals(currentUserId);
    }
}
