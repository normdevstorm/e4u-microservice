package com.renting.authentication_service.config.security.filter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.renting.authentication_service.dto.user.LoginRequestDto;
import com.renting.authentication_service.entity.Key;
import com.renting.authentication_service.entity.Payload;
import com.renting.authentication_service.entity.User;
import com.renting.authentication_service.response.GenericResponse;
import com.renting.authentication_service.repository.UserRepository;
import com.renting.authentication_service.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

//@Log4j2
@Slf4j
// Handle those request without token, as of now they include '/login' endpoint only
public class LocalAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    Logger logger = LoggerFactory.getLogger(LocalAuthenticationFilter.class);


    //TODO : config snakecase for objectmapper to use it as bean
    private final ObjectMapper objectMapper = new ObjectMapper();
    public LocalAuthenticationFilter(AuthenticationManager authenticationManager, JwtService jwtService, UserRepository userRepository, String filterProcessesUrl) {
        super(authenticationManager);
        super.setFilterProcessesUrl(filterProcessesUrl);
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        // retrieve username + password
        try {
            LoginRequestDto requestBody = new ObjectMapper().readValue(request.getInputStream(), LoginRequestDto.class);
            String username = requestBody.getUsername();
            String password = requestBody.getPassword();

            UsernamePasswordAuthenticationToken authenticationRequest = new UsernamePasswordAuthenticationToken(username, password);
            AuthenticationManager authenticationManager = getAuthenticationManager();
            return authenticationManager.authenticate(authenticationRequest);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        /*
            get username -> get keys -> gen tokens
         */
        User userPrinciple =  (User)authResult.getPrincipal();
        String username = userPrinciple.getUsername();
        User userWithKey = userRepository.findByUsername(username).orElseThrow();

        Key key = userWithKey.getKey();
        String privateKeyPEM = key.getPrivateKey();
        UUID id = userWithKey.getUserId();


        // Build Payload, Version ++
        String role = userWithKey.getRole().name();
        Integer refreshTokenVersion = key.getRefreshTokenVersion() + 1;
        Integer accessTokenVersion = key.getAccessTokenVersion() + 1;
        Payload payloadForAccessToken = Payload.builder().version(accessTokenVersion).id(id).role(role).authorities(userWithKey.getRole().getAuthorities()).username(username).build();
        Payload payloadForRefreshToken = Payload.builder().version(refreshTokenVersion).id(id).role(role).authorities(userWithKey.getRole().getAuthorities()).username(username).build();

        String accessToken = jwtService.generateAccessToken(payloadForAccessToken, privateKeyPEM);
        String refreshToken = jwtService.generateRefreshToken(payloadForRefreshToken, privateKeyPEM);
        jwtService.updateRefreshToken(refreshToken, id);
        jwtService.updateRefreshTokenVersion(id, refreshTokenVersion);
        jwtService.updateAccessTokenVersion(id, refreshTokenVersion);

        Map<String, String> tokens = Map.of("accessToken", accessToken, "refreshToken", refreshToken);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(GenericResponse.builder().success(true).data(tokens).message("Login succeeded").build()));
        response.setStatus(HttpServletResponse.SC_OK);
        //TODO: SWITCH TO SP4J2 LATER ON
//        UtilsManager.getUserIdContextLog(userPrinciple.getUserId().toString(), "User authenticated");
        log.info("User {} authenticated", userPrinciple.getUserId());

    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(GenericResponse.builder().success(false).message("Login failed").build()));
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }
}
