package com.renting.authentication_service.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.renting.authentication_service.common.exception.custom.CustomJwtException;
import com.renting.authentication_service.entity.Payload;
import com.renting.authentication_service.entity.User;
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
    @Value("${security.jwt.secret-key}")
    private String secretKey;
    @Value("${security.jwt.access-token-expiration-time}")
    private long accessTokenExpiration;
    @Value("${security.jwt.refresh-token-expiration-time}")
    private long refreshTokenExpiration;
    private final KeyService keyService;
    private final ObjectMapper objectMapper = new ObjectMapper();


    @Autowired
    public JwtService(KeyService keyService) {
        this.keyService = keyService;
    }
    //create a secret key then to keep it private
    //tools: https://asecuritysite.com/encryption/plain
    /*
        Implementing JWT with key pairs + access - refresh token
     */
    // generate key pairs

    public Map<String, String> generateKeyPair() {
        KeyPairGenerator keyPairGenerator = null;
        try {
            keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            log.error("Can not generate key pair",e);
            throw new CustomJwtException("Can not generate key pair", e);
        }
        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        PrivateKey privateKey = keyPair.getPrivate();
        PublicKey publicKey = keyPair.getPublic();

        // convert keys to PEM (which has header and footer) for storing in database
        // encode to bytes ->  to string
        String privateKeyPEM = Base64.getEncoder().encodeToString(privateKey.getEncoded());
        String publicKeyPEM = Base64.getEncoder().encodeToString(publicKey.getEncoded());

        Map<String, String> keyPairMap = new HashMap<>();
        keyPairMap.put("privateKey", privateKeyPEM);
        keyPairMap.put("publicKey", publicKeyPEM);

        return keyPairMap;
    }

    private String generateJwtToken(Payload payload, String privateKeyPEM, long exprirationMillis) {
        // convert PEM string format back to PrivateKey again
        PrivateKey privateKey;
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec( Base64.getDecoder().decode(privateKeyPEM));
            privateKey = keyFactory.generatePrivate(keySpec);
            return Jwts.builder().claims(objectMapper.convertValue(payload, Map.class)).subject(payload.getUsername()).issuedAt(new Date(System.currentTimeMillis())).expiration(new Date(System.currentTimeMillis() + exprirationMillis )).signWith(privateKey, Jwts.SIG.RS256).compact();
        } catch (NoSuchAlgorithmException e) {
            log.error("Can not generate token");
            throw new RuntimeException(e);
        } catch (InvalidKeySpecException e) {
            log.error("Can not generate token");
            throw new RuntimeException(e);
        }

    }
    //generate access and refresh

    public String generateAccessToken(Payload payload, String privateKeyPEM){
        return generateJwtToken(payload, privateKeyPEM, accessTokenExpiration);
    }

    public String generateRefreshToken(Payload payload, String privateKeyPEM){
        return generateJwtToken(payload, privateKeyPEM, refreshTokenExpiration);
    }

    private static PublicKey convertPublicKeyFromPEM(String publicKeyPEM, KeyFactory keyFactory) throws InvalidKeySpecException {
        PublicKey publicKey;
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(publicKeyPEM));
        publicKey = keyFactory.generatePublic(keySpec);
        return publicKey;
    }

    private Claims extractClaim(String token, boolean isRefreshToken) {
        Claims claims = null;
        try {
            claims = Jwts.parser()
                    .setSigningKeyResolver(
                            new SigningKeyResolverAdapter() {
                                @Override
                                public Key resolveSigningKey(JwsHeader header, Claims claims) {
                                    String username = claims.get("username", String.class);
                                    PublicKey publicKey = keyService.getPublicKeyByUsername(username);
                                    return publicKey;
                                }
                            }
                    )
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        }  catch (IllegalArgumentException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new CustomJwtException(e.getMessage(), e);
        }
        int version = claims.get("version", Integer.class);
        if(verifyTokenVersion(isRefreshToken, version,claims)){
            return claims;
        }
        return null;
    }



    public String extractUsername(String token, boolean isRefreshToken) {
        String username = null;
        try {
            username = Objects.requireNonNull(extractClaim(token, isRefreshToken)).getSubject();
        } catch (NullPointerException e) {
            log.error("Can not extract username from claim, username = null !!!");
            throw new BadCredentialsException("Authentication failed !!!");}
        catch (ExpiredJwtException e) {
            ThreadContext.put("userId", "[ userId: " + e.getClaims().get("id", String.class) + " ]");
            log.error(e.getMessage());
            ThreadContext.clearMap();
            throw new CustomJwtException("User not authenticated",e);
        }
        return username;
    }

    private Date extractExpiration(String token, boolean isRefreshToken) {
        Claims claims = extractClaim(token, isRefreshToken);
        if(claims != null){
            return claims.getExpiration();
        } else {
            log.error("Can not resolve token!!!");
            throw new CustomJwtException("Claim is null due to token resolve failure", new NullPointerException());
        }
    }

    private boolean verifyTokenVersion(boolean isRefreshToken,int version, Claims claims) {
        try {
            if(isRefreshToken){
                return version == keyService.getKeyByUsername(claims.getSubject()).getRefreshTokenVersion();
            }
            return version == keyService.getKeyByUsername(claims.getSubject()).getAccessTokenVersion();
        } catch (Exception e) {
            log.error("Can not verify token version !!!");
            return false;
        }
    }


    public boolean isTokenValid(String token, boolean isRefreshToken) {
        return !isTokenExpired(token, isRefreshToken);
    }

    private boolean isTokenExpired(String token, boolean isRefreshToken) {
        return extractExpiration(token,isRefreshToken ).before(new Date());
    }


    public User getUserFromContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return ((User) authentication.getPrincipal());
    }

    public void updateRefreshToken(String refreshToken, UUID userId){
        keyService.updateRefreshToken(refreshToken, userId);
    }

    public void updateRefreshTokenVersion(UUID userId, int refreshTokenVersion){
        keyService.updateRefreshTokenVersion(userId, refreshTokenVersion);
    }

    public void updateAccessTokenVersion(UUID userId, int accessTokenVersion){
        keyService.updateAccessTokenVersion(userId, accessTokenVersion);
    }
}
