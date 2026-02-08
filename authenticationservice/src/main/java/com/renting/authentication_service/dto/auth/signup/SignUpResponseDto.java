package com.renting.authentication_service.dto.auth.signup;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.renting.authentication_service.entity.User;
import lombok.*;

import java.io.Serializable;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SignUpResponseDto implements Serializable {
    private User user;
    private String accessToken;
    private String refreshToken;
}
