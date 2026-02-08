package com.renting.authentication_service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "keyTokens")
public class Key {
    @Id
    private UUID id;

    @OneToOne
    @JoinColumn(referencedColumnName = "userId", unique = true)
    private User user;

    @Column(name = "public_key", nullable = false, length = 4096)
    private String publicKey;

    @Column(name = "private_key", nullable = false, length = 4096)
    private String privateKey;

    @Column(name = "refresh_token", nullable = false, length = 2048)
    private String refreshToken;

    @Column(name = "refresh_token_version", nullable = false)
    private Integer refreshTokenVersion;
    @Column(name = "access_token_version", nullable = false)
    private Integer accessTokenVersion;

}
