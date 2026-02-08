package com.renting.item_service.model.response.auth;

import lombok.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.UUID;
@NoArgsConstructor
@Setter @Getter
public class UserModelFromJwtPayload {
    private UUID id;
    private String role;
    private List<SimpleGrantedAuthority> authorities;
    private Integer version;
    private String username;
}
