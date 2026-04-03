package com.fintech.service;

import com.fintech.dto.AuthResponse;
import com.fintech.dto.LoginRequest;
import org.springframework.security.core.userdetails.UserDetails;

public interface AuthenticationService {
    /**
     * Authenticates user and generates JWT token
     * @throws com.fintech.exception.AuthenticationException if credentials invalid
     * @throws com.fintech.exception.RateLimitException if rate limit exceeded
     */
    AuthResponse authenticate(LoginRequest request);

    /**
     * Validates JWT token and extracts user details
     * @throws com.fintech.exception.InvalidTokenException if token invalid or expired
     */
    UserDetails validateToken(String token);

    /**
     * Generates new JWT token for user
     */
    String generateToken(Long userId, String username, String role);
}
