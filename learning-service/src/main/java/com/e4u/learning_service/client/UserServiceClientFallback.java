// package com.e4u.learning_service.client;

// import com.e4u.learning_service.client.dto.UserResponse;
// import lombok.extern.slf4j.Slf4j;
// import org.springframework.stereotype.Component;

// import java.util.UUID;

// /**
// * Fallback implementation for UserServiceClient.
// *
// * TODO: Implement proper fallback logic when user-service is available.
// * Currently returns default values to allow the service to function
// * without the user-service dependency.
// */
// @Slf4j
// @Component
// public class UserServiceClientFallback implements UserServiceClient {

// @Override
// public UserResponse getUserById(UUID userId) {
// log.warn("Fallback: Unable to fetch user {}. User service may be
// unavailable.", userId);
// // Return a placeholder response
// return UserResponse.builder()
// .userId(userId)
// .username("unknown")
// .email("unknown@example.com")
// .build();
// }

// @Override
// public Boolean userExists(UUID userId) {
// log.warn("Fallback: Unable to verify user {}. Assuming user exists.",
// userId);
// // Default to true to allow operations to proceed
// // TODO: Review this behavior based on security requirements
// return true;
// }
// }
