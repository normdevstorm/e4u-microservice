package com.renting.authentication_service.dto.auth.refresh_token;

import lombok.*;

import java.io.Serializable;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RefreshTokenResponse implements Serializable {
    private String accessToken;
    private String refreshToken;
}
