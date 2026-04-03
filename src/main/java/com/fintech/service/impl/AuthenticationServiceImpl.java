package com.fintech.service.impl;

import com.fintech.dto.AuthResponse;
import com.fintech.dto.LoginRequest;
import com.fintech.exception.AuthenticationException;
import com.fintech.model.User;
import com.fintech.repository.UserRepository;
import com.fintech.service.AuthenticationService;
import com.fintech.service.RateLimiterService;
import com.fintech.util.JwtUtil;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationServiceImpl.class);

    private final UserRepository userRepository;
    private final RateLimiterService rateLimiterService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthenticationServiceImpl(
        UserRepository userRepository,
        RateLimiterService rateLimiterService,
        PasswordEncoder passwordEncoder,
        JwtUtil jwtUtil
    ) {
        this.userRepository = userRepository;
        this.rateLimiterService = rateLimiterService;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public AuthResponse authenticate(LoginRequest request) {
        logger.debug("Authentication attempt for username: {}", request.getUsername());
        
        // Check rate limit
        rateLimiterService.checkRateLimit(request.getUsername());

        // Retrieve user
        User user = userRepository.findByUsername(request.getUsername())
            .orElse(null);

        if (user == null) {
            logger.warn("User not found: {}", request.getUsername());
            rateLimiterService.recordFailedAttempt(request.getUsername());
            throw new AuthenticationException("Invalid credentials");
        }
        
        logger.debug("User found: {}, isActive: {}", user.getUsername(), user.getIsActive());
        
        if (!user.getIsActive()) {
            logger.warn("User is not active: {}", request.getUsername());
            rateLimiterService.recordFailedAttempt(request.getUsername());
            throw new AuthenticationException("Invalid credentials");
        }

        // Verify password
        logger.debug("Verifying password for user: {}", request.getUsername());
        logger.debug("Stored password hash: {}", user.getPasswordHash());
        boolean passwordMatches = passwordEncoder.matches(request.getPassword(), user.getPasswordHash());
        logger.debug("Password matches: {}", passwordMatches);
        
        if (!passwordMatches) {
            logger.warn("Password mismatch for user: {}", request.getUsername());
            rateLimiterService.recordFailedAttempt(request.getUsername());
            throw new AuthenticationException("Invalid credentials");
        }

        // Success - reset rate limiter and generate token
        logger.info("Authentication successful for user: {}", request.getUsername());
        rateLimiterService.resetFailedAttempts(request.getUsername());
        String token = jwtUtil.generateToken(user);

        return new AuthResponse(token, 900); // 15 minutes in seconds
    }

    @Override
    public UserDetails validateToken(String token) {
        String username = jwtUtil.extractUsername(token);
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new AuthenticationException("User not found"));

        return new org.springframework.security.core.userdetails.User(
            user.getUsername(),
            user.getPasswordHash(),
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
        );
    }

    @Override
    public String generateToken(Long userId, String username, String role) {
        if (userId == null) {
            throw new AuthenticationException("User ID cannot be null");
        }
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new AuthenticationException("User not found"));
        return jwtUtil.generateToken(user);
    }
}
