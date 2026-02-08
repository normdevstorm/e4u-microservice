package com.renting.authentication_service.controller;

import com.renting.authentication_service.dto.auth.refresh_token.RefreshTokenResponse;
import com.renting.authentication_service.dto.auth.signup.SignUpResponseDto;
import com.renting.authentication_service.dto.user.LoginRequestDto;
import com.renting.authentication_service.dto.user.UserRequestDto;
import com.renting.authentication_service.response.GenericResponse;
import com.renting.authentication_service.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RequestMapping()
@RestController
@Log4j2
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> register(@Valid @RequestBody UserRequestDto registerUserDto) {
        try{
        SignUpResponseDto signUpResponseDto = authenticationService.signup(registerUserDto);
         GenericResponse<SignUpResponseDto> genericResponse =  GenericResponse.<SignUpResponseDto>builder().data(signUpResponseDto).success(true).message("Sign up successfully").build();
        return ResponseEntity.ok(genericResponse);
        } catch (ResponseStatusException e){
            log.error(e.getMessage());
           return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
        }
    }
    @PostMapping("/login")
    public void authenticate(@RequestBody LoginRequestDto loginRequestDto) {
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<RefreshTokenResponse> refreshToken(@RequestBody Map<String, String> refreshToken) {
         RefreshTokenResponse refreshTokenResponse = authenticationService.refreshToken(refreshToken.get("refresh_token"));
         return ResponseEntity.ok(refreshTokenResponse);
    }

    @PostMapping("/validate-token")
    public ResponseEntity<GenericResponse<Boolean>> validateToken(@RequestBody Map<String, String> token) {
            String jwtToken = token.get("token");
            boolean isValid = authenticationService.validateToken(jwtToken);
            if(isValid){
               return ResponseEntity.ok( GenericResponse.<Boolean>builder().data(true).success(true).message("Token valid").build()) ;
            }
            return ResponseEntity.ok(GenericResponse.<Boolean>builder().success(true).data(false).message("Token invalid").build());
        }

    // TODO: Handle log out here
}