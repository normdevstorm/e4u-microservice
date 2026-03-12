package com.renting.authentication_service.dto.user;

import com.renting.authentication_service.common.enums.Role;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserRequestDto {
    @NotBlank(message = "Username is mandatory")
    private String username;
    @NotBlank(message = "Password is mandatory")
    private String password;
    @NotNull(message = "Role is mandatory")
    private Role role = Role.LEARNER;
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

    @AssertTrue(message = "Only LEARNER or TEACHER roles are allowed during registration")
    public boolean isRoleValidForSignup() {
        return role == null || role == Role.LEARNER || role == Role.TEACHER;
    }
}
