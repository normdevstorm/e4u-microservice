package com.e4u.learning_service.common.utils;

import com.e4u.learning_service.common.exception.AppException;
import com.e4u.learning_service.common.exception.ErrorCode;
import com.e4u.learning_service.dtos.response.UserModelFromJwtPayload;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

/**
 * Utility class for security-related operations.
 * Provides methods to extract user information from SecurityContext.
 */
@Log4j2
public class SecurityUtil {

    private SecurityUtil() {
        // Private constructor to prevent instantiation
    }

    /**
     * Get the current authenticated user's ID from SecurityContext.
     *
     * @return UUID of the current user
     * @throws AppException if user is not authenticated or authentication is
     *                      invalid
     */
    public static UUID getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            log.error("No authenticated user found in SecurityContext");
            throw new AppException(ErrorCode.UNAUTHORIZED, "User not authenticated");
        }

        Object principal = authentication.getPrincipal();

        if (!(principal instanceof UserModelFromJwtPayload)) {
            log.error("Invalid principal type in SecurityContext: {}",
                    principal != null ? principal.getClass().getName() : "null");
            throw new AppException(ErrorCode.UNAUTHORIZED, "Invalid authentication principal");
        }

        UserModelFromJwtPayload user = (UserModelFromJwtPayload) principal;
        UUID userId = user.getId();

        if (userId == null) {
            log.error("User ID is null in authentication principal");
            throw new AppException(ErrorCode.UNAUTHORIZED, "User ID not found in authentication");
        }

        log.debug("Retrieved user ID from SecurityContext: {}", userId);
        return userId;
    }

    /**
     * Get the current authenticated user's model from SecurityContext.
     *
     * @return UserModelFromJwtPayload of the current user
     * @throws AppException if user is not authenticated or authentication is
     *                      invalid
     */
    public static UserModelFromJwtPayload getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            log.error("No authenticated user found in SecurityContext");
            throw new AppException(ErrorCode.UNAUTHORIZED, "User not authenticated");
        }

        Object principal = authentication.getPrincipal();

        if (!(principal instanceof UserModelFromJwtPayload)) {
            log.error("Invalid principal type in SecurityContext: {}",
                    principal != null ? principal.getClass().getName() : "null");
            throw new AppException(ErrorCode.UNAUTHORIZED, "Invalid authentication principal");
        }

        return (UserModelFromJwtPayload) principal;
    }

    /**
     * Get the current authenticated user's username from SecurityContext.
     *
     * @return username of the current user
     * @throws AppException if user is not authenticated or authentication is
     *                      invalid
     */
    public static String getCurrentUsername() {
        UserModelFromJwtPayload user = getCurrentUser();
        String username = user.getUsername();

        if (username == null || username.isEmpty()) {
            log.error("Username is null or empty in authentication principal");
            throw new AppException(ErrorCode.UNAUTHORIZED, "Username not found in authentication");
        }

        return username;
    }

    /**
     * Get the current authenticated user's role from SecurityContext.
     *
     * @return role of the current user
     * @throws AppException if user is not authenticated or authentication is
     *                      invalid
     */
    public static String getCurrentUserRole() {
        UserModelFromJwtPayload user = getCurrentUser();
        return user.getRole();
    }
}
