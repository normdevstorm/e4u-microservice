// package com.e4u.learning_service.client;

// import com.e4u.learning_service.client.dto.UserResponse;
// import org.springframework.cloud.openfeign.FeignClient;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.PathVariable;

// import java.util.Optional;
// import java.util.UUID;

// /**
// * Feign client for communicating with the User Service.
// *
// * TODO: Update the URL when user-service is implemented.
// * The URL can be configured via application.yaml with the property:
// * client.user-service.url
// *
// * <p>
// * This client provides methods to validate and retrieve user information
// * from the user-service microservice.
// * </p>
// */
// @FeignClient(name = "user-service", url =
// "${client.user-service.url:http://localhost:8081}", fallback =
// UserServiceClientFallback.class)
// public interface UserServiceClient {

// /**
// * Get user by ID.
// *
// * @param userId The UUID of the user
// * @return User response if found
// */
// @GetMapping("/v1/users/{userId}")
// UserResponse getUserById(@PathVariable("userId") UUID userId);

// /**
// * Check if user exists.
// *
// * @param userId The UUID of the user
// * @return true if user exists, false otherwise
// */
// @GetMapping("/v1/users/{userId}/exists")
// Boolean userExists(@PathVariable("userId") UUID userId);
// }
