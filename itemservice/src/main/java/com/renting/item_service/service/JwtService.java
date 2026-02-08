package com.renting.item_service.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.renting.item_service.common.exception.custom.CustomJwtException;
import io.jsonwebtoken.*;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;

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
            throw new CustomJwtException(e.getMessage(), e);
        }
    }

}
