package com.renting.item_service.repository;

import com.renting.item_service.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ItemRepository extends JpaRepository<Item, UUID> {
    @Query("SELECT i FROM Item i WHERE i.ownerId = :ownerId")
    List<Item> findAllByOwnerId(UUID ownerId);
    @Query("SELECT i FROM Item i WHERE i.id = :id")
    Optional<Object> findItemById(UUID id);
}