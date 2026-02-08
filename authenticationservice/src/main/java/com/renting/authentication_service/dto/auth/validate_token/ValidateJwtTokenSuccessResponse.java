package com.renting.authentication_service.dto.auth.validate_token;

import com.renting.authentication_service.dto.user.UserResponseDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

@AllArgsConstructor
@Getter @Setter
public class ValidateJwtTokenSuccessResponse extends UserResponseDto {
    private List<SimpleGrantedAuthority> authorities;
}
