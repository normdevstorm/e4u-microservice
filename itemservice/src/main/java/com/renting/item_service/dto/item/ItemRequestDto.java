package com.renting.item_service.dto.item;

import com.renting.item_service.common.enums.AvailabilityStatus;
import com.renting.item_service.common.enums.Category;
import com.renting.item_service.entity.Item;
import lombok.Getter;
import lombok.Setter;
import lombok.Value;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO for {@link Item}
 */
@Getter
@Setter
@Value
public class ItemRequestDto implements Serializable {
    boolean isActive;
    LocalDateTime createdAt;
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
}