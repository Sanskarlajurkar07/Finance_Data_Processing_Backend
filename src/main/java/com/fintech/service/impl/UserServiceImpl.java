package com.fintech.service.impl;

import com.fintech.annotation.Auditable;
import com.fintech.dto.CreateUserRequest;
import com.fintech.exception.ConflictException;
import com.fintech.exception.RecordNotFoundException;
import com.fintech.model.ActionType;
import com.fintech.model.Role;
import com.fintech.model.User;
import com.fintech.repository.UserRepository;
import com.fintech.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class UserServiceImpl implements UserService {

    private static final int MIN_PASSWORD_LENGTH = 8;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    @Auditable(actionType = ActionType.CREATE)
    public User createUser(CreateUserRequest request) {
        // Validate password length
        if (request.getPassword().length() < MIN_PASSWORD_LENGTH) {
            throw new IllegalArgumentException("Password must be at least " + MIN_PASSWORD_LENGTH + " characters");
        }

        // Check username uniqueness
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new ConflictException("Username already exists: " + request.getUsername());
        }

        // Check email uniqueness
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("Email already exists: " + request.getEmail());
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());
        user.setIsActive(true);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        return userRepository.save(user);
    }

    @Override
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
            .orElseThrow(() -> new RecordNotFoundException("User not found: " + username));
    }

    @Override
    @Transactional
    @Auditable(actionType = ActionType.UPDATE)
    public void deactivateUser(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RecordNotFoundException("User not found"));

        user.setIsActive(false);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    @Override
    @Transactional
    public User updateUserRole(Long userId, Role newRole) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RecordNotFoundException("User not found"));

        user.setRole(newRole);
        user.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }
}
