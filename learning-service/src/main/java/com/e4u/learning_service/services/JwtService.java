package com.e4u.learning_service.services;

import com.e4u.learning_service.common.exception.AppException;
import com.e4u.learning_service.common.exception.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.*;
import lombok.extern.log4j.Log4j2;
import java.util.*;

import org.springframework.stereotype.Service;

@Service
@Log4j2
public class JwtService {
    // TODO: Create a bean of mapper and inject it here
    private final ObjectMapper objectMapper = new ObjectMapper();
    // @Value("b7e48ab258606b5cdcac9679ece1f6294259ad079e4c22b3679e82c3f0e5d2f1")
    // private final String secretKey;

    public JwtService() {
    }
    // create a secret key then to keep it private
    // tools: https://asecuritysite.com/encryption/plain

    public Claims extractClaim(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3)
                throw new IllegalArgumentException("Invalid JWT format");
            String payload = new String(Base64.getUrlDecoder().decode(parts[1]));
            ObjectMapper mapper = new ObjectMapper();
            @SuppressWarnings("unchecked")
            Map<String, Object> claimsMap = mapper.readValue(payload, Map.class);
            Claims claims = Jwts.claims().add(claimsMap).build();
            return claims;
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new AppException(ErrorCode.UNAUTHORIZED, "Failed to extract claims from token");
        }
    }

}