package com.renting.authentication_service.service;

import com.renting.authentication_service.entity.User;
import com.renting.authentication_service.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    @Autowired
    public UserService(
            UserRepository userRepository
    ) {
        this.userRepository = userRepository;
    }

    public User findUserById(UUID userId) {
        try {
            return userRepository.findByUserId(userId);
        } catch (Exception e) {
            throw new RuntimeException("User not found with id: " + userId);
        }
    }
}
