package com.renting.authentication_service.dto.user;

import com.renting.authentication_service.common.enums.Role;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;

@Data
public class UserRequestDto {
    @NotBlank(message = "Username is mandatory")
    private String username;
    @NotBlank(message = "Password is mandatory")
    private String password;
    @NotNull
    @Enumerated(EnumType.STRING)
    @Value("USER")
    private Role role;
    @NotBlank
    @Size(max = 50, message = "Name should not exceed 50 character length")
    private String firstName;
    @NotBlank
    @Size(max = 50, message = "Name should not exceed 50 character length")
    private String lastName;
    @NotBlank
    private String phoneNumber;
    @Email(message = "Email needs to conform to this format : abc@domain.com")
    private String email;
}
