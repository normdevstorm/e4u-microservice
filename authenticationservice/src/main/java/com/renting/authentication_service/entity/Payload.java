package com.renting.authentication_service.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Payload implements Serializable {
    private UUID id;
    private String role;
    private List<SimpleGrantedAuthority> authorities;
    private Integer version;
    private String username;

}
