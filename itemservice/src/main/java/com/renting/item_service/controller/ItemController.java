package com.renting.item_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.renting.item_service.client.AuthenticationService;
import com.renting.item_service.dto.item.ItemRequestDto;
import com.renting.item_service.dto.item.ItemResponseDto;
import com.renting.item_service.model.response.GenericResponse;
import com.renting.item_service.service.ItemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@Slf4j
@RestController
@RequestMapping("")
public class ItemController {

    @Autowired
    private ItemService itemService;

    @Autowired
    private AuthenticationService authenticationService;

    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    @PostMapping("/add")
    public ResponseEntity<GenericResponse<ItemResponseDto>> addItem(@RequestBody ItemRequestDto itemRequestDto) {
        ItemResponseDto itemResponseDto = itemService.addItem(itemRequestDto);
        return ResponseEntity.ok( GenericResponse.<ItemResponseDto>builder().data(itemResponseDto).message("Item added successfully").success(true).build());
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER', 'RENTER')")
    @GetMapping("/all")
    public ResponseEntity<GenericResponse<List<ItemResponseDto>>> getAllItems() {
        List<ItemResponseDto> items = itemService.getAllItems();
        return ResponseEntity.ok(GenericResponse.<List<ItemResponseDto>>builder().data(items).message("All items retrieved successfully").success(true).build());
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER') and @postSecurity.isItemOwner(#itemId, authentication.principal.id)")
    @GetMapping("/{itemId}")
    public ResponseEntity<GenericResponse<ItemResponseDto>> getItemById(@PathVariable String itemId) {
        ItemResponseDto item = itemService.getItemById(itemId);
        return ResponseEntity.ok(GenericResponse.<ItemResponseDto>builder().data(item).message("Item retrieved successfully").success(true).build());
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER') and @postSecurity.isItemOwner(#itemId, authentication.principal.id)")
    @DeleteMapping("/{itemId}")
    public ResponseEntity<GenericResponse<Void>> deleteItem(@PathVariable String itemId) {
        itemService.deleteItem(itemId);
        return ResponseEntity.ok(GenericResponse.<Void>builder().message("Item deleted successfully").success(true).build());
    }

    @PostMapping("validate-token")
    public ResponseEntity<?> testValidateTokenViaFeignClient(@RequestBody String token) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            ResponseEntity<?> response = authenticationService.validateToken(token);
            log.info(Objects.requireNonNull(response.getBody()).toString());
            return ResponseEntity.ok(response.getBody());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
