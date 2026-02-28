package com.e4u.learning_service.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

/**
 * Composite primary key for UnitBaseWord entity.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UnitBaseWordId implements Serializable {

    private UUID unitId;
    private UUID wordId;
}
