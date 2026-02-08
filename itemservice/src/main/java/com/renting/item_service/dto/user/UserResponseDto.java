package com.renting.item_service.dto.user;

import com.renting.item_service.common.enums.Role;
import lombok.Data;

import java.io.Serializable;
import java.util.UUID;

@Data
public class UserResponseDto implements Serializable {
    private UUID userId;
    private String username;
    private Role role;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String email;
}
