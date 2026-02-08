package com.renting.item_service.service;

import com.renting.item_service.model.response.auth.UserModelFromJwtPayload;
import org.springframework.stereotype.Service;

@Service
public class SecurityContextService {

    public UserModelFromJwtPayload getCurrentUser() {
        return (UserModelFromJwtPayload) org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
