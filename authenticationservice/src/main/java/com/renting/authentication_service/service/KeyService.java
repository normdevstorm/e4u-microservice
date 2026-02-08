package com.renting.authentication_service.service;

import com.renting.authentication_service.entity.Key;
import com.renting.authentication_service.repository.KeyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.UUID;

@Service
public class KeyService {
    private final KeyRepository keyRepository;
    @Autowired
    public KeyService(KeyRepository keyRepository) {
        this.keyRepository = keyRepository;
    }


    public Key saveKey(Key key){
        return keyRepository.save(key);
    }
    public PublicKey getPublicKeyByUsername(String username){
        Key key = keyRepository.findByUser_Username(username);
        String publicKeyPEM = key.getPublicKey();
        try {
            //TODO: move this code blocks to be in specialized function
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(publicKeyPEM));
            PublicKey publicKey = keyFactory.generatePublic(keySpec);
            return publicKey;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }

    public Key getKeyByUsername(String username){
        return keyRepository.findByUser_Username(username);
    }

    public int getRefreshTokenVersionByUsername(String username){
        Key key = keyRepository.findByUser_Username(username);
        return key.getRefreshTokenVersion();
    }

    public int getVersioningByUserId(UUID userId){
        Key key = keyRepository.findByUser_UserId(userId);
        return key.getRefreshTokenVersion();
    }

    public void updateRefreshTokenVersion(UUID userId, Integer refreshTokenVersion){
        keyRepository.updateRefreshTokenVersionById(refreshTokenVersion, userId);
    }

    public void updateAccessTokenVersion(UUID userId, Integer accessTokenVersion){
        keyRepository.updateAccessTokenVersionByUserId(accessTokenVersion, userId);
    }

    public void updateRefreshToken(String refreshToken, UUID userId){
        keyRepository.updateRefreshTokenByUserId(refreshToken, userId);
    }


    public void updateAccessTokenVersionByUsername(String username,Integer version){
        Key key = keyRepository.findByUser_Username(username);
        keyRepository.updateAccessTokenVersionByUsername(version,username);
    }


}
