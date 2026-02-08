package com.renting.authentication_service.dto.user;

import lombok.Data;

@Data
public class LoginRequestDto {
    private String username;
    private String password;
}
