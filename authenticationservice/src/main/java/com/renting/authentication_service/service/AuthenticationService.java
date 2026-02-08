package com.renting.authentication_service.service;

import com.renting.authentication_service.common.exception.custom.CustomJwtException;
import com.renting.authentication_service.dto.auth.refresh_token.RefreshTokenResponse;
import com.renting.authentication_service.dto.auth.signup.SignUpResponseDto;
import com.renting.authentication_service.dto.auth.validate_token.ValidateJwtTokenSuccessResponse;
import com.renting.authentication_service.dto.user.UserRequestDto;
import com.renting.authentication_service.entity.Key;
import com.renting.authentication_service.entity.Payload;
import com.renting.authentication_service.entity.User;
import com.renting.authentication_service.mapper.signup.SignUpMapper;
import com.renting.authentication_service.mapper.user.UserRequestMapper;
import com.renting.authentication_service.mapper.user.UserResponseMapper;
import com.renting.authentication_service.repository.UserRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;

@Log4j2
@Transactional
@Service
public class AuthenticationService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;
    private final UserRequestMapper userRequestMapper;
    private final UserResponseMapper userResponseMapper;
    private final JwtService jwtService;

    private final SignUpMapper signUpMapper;
    private KeyService keyService;


    @Autowired
    public AuthenticationService(
            UserRepository userRepository,
            AuthenticationManager authenticationManager,
            PasswordEncoder passwordEncoder,
            UserRequestMapper userRequestMapper,
            JwtService jwtService,
            SignUpMapper signUpMapper,
            KeyService keyService,
            UserResponseMapper userResponseMapper
            ) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userRequestMapper = userRequestMapper;
        this.jwtService = jwtService;
        this.signUpMapper = signUpMapper;
        this.keyService = keyService;
        this.userResponseMapper = userResponseMapper;
    }

    public SignUpResponseDto signup(UserRequestDto input) {
        /*
            Register info -> create key pairs -> save users -> build payload -> create token -> return token + user
         */
        User userRequest = userRequestMapper.toUser(input);
        userRequest.setPassword(passwordEncoder.encode(input.getPassword()));
        userRequest.setCreatedAt(LocalDateTime.now());
        userRequest.setActive(true);
        User user = userRepository.save(userRequest);
        // generate key pair
        Map<String , String> keyPair = jwtService.generateKeyPair();
        String publicKeyPEM = keyPair.get("publicKey");
        String privateKeyPEM = keyPair.get("privateKey");
        // set initial version
        int version = 0;
        Payload payload = Payload.builder().version(0).id(user.getUserId()).role(user.getRole().name()).authorities(user.getRole().getAuthorities()).username(user.getUsername()).version(version).build();
        String accessToken = jwtService.generateAccessToken(payload, privateKeyPEM);
        String refreshToken =  jwtService.generateRefreshToken(payload, privateKeyPEM);
        // save key
        Key key = Key.builder().publicKey(publicKeyPEM).privateKey(privateKeyPEM).user(user).id(user.getUserId()).refreshTokenVersion(version).accessTokenVersion(version).refreshToken(refreshToken).publicKey(publicKeyPEM).privateKey(privateKeyPEM).build();
        keyService.saveKey(key);
        return signUpMapper.toSignUpResponseDto(user, accessToken, refreshToken);
    }

    public RefreshTokenResponse refreshToken(String refreshToken){
        String username = jwtService.extractUsername(refreshToken, true);
        Key key = keyService.getKeyByUsername(username);
        if(key == null){
            throw new RuntimeException("Invalid refresh token");
        }
        String privateKeyPEM = key.getPrivateKey();
        int accessTokenVersion = key.getAccessTokenVersion();

        Payload payloadForAccessToken = Payload.builder().version(accessTokenVersion + 1).id(key.getId()).role(key.getUser().getRole().name()).authorities(key.getUser().getRole().getAuthorities()).username(key.getUser().getUsername()).build();
        String accessToken = jwtService.generateAccessToken(payloadForAccessToken, privateKeyPEM);
        key.setAccessTokenVersion(accessTokenVersion + 1);
        keyService.saveKey(key);
        return RefreshTokenResponse.builder().accessToken(accessToken).refreshToken(key.getRefreshToken()).build();
    }

    public boolean validateToken(String jwtToken){
        try {
            return jwtService.isTokenValid(jwtToken, false);
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }
    }

    public ValidateJwtTokenSuccessResponse getUserFromToken(String jwtToken) {
        String username = jwtService.extractUsername(jwtToken, false);
        User user = userRepository.findByUsername(username).orElseThrow(() -> new CustomJwtException("User not found"));
        return userResponseMapper.toValidateJwtTokenSuccessResponseDto(user);
    }


    private void updatePassword(User user, String newPassword) {
        String encodedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encodedPassword);
//        user.setResetPasswordToken(null);
        userRepository.save(user);
    }

}
