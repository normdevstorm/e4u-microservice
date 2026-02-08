package com.renting.item_service.entity;

import jakarta.persistence.MappedSuperclass;
import lombok.Data;

import java.time.LocalDateTime;
@Data
@MappedSuperclass
public class BaseEntity {
    private boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
