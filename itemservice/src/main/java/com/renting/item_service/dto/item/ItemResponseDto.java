package com.renting.item_service.dto.item;

import com.renting.item_service.common.enums.AvailabilityStatus;
import com.renting.item_service.common.enums.Category;
import lombok.Value;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Value
public class ItemResponseDto implements Serializable {
    UUID id;
    UUID ownerId;
    Category category;
    String name;
    String description;
    double price;
    double latePrice;
    double depositAmount;
    double amount;
    String address;
    int conditionRating;
    AvailabilityStatus status;
    boolean isActive;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}