package com.renting.item_service.entity;

import com.renting.item_service.common.enums.AvailabilityStatus;
import com.renting.item_service.common.enums.Category;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@Table(name = "items")
public class Item extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private UUID ownerId;
    @Enumerated(EnumType.STRING)
    private Category category;
    private String name;
    private String description;
    private double price;
    //TODO: discuss with team on the name of this var
    private double latePrice;
    private double depositAmount;
    private double amount;
    private String address;
    private int conditionRating;
    @Enumerated(EnumType.STRING)
    private AvailabilityStatus status = AvailabilityStatus.AVAILABLE;
}
