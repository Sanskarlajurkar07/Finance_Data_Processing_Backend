package com.fintech.service;

import com.fintech.dto.AuthResponse;
import com.fintech.dto.LoginRequest;
import com.fintech.exception.AuthenticationException;
import com.fintech.model.Role;
import com.fintech.model.User;
import com.fintech.repository.UserRepository;
import com.fintech.service.impl.AuthenticationServiceImpl;
import com.fintech.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthenticationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RateLimiterService rateLimiterService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    private AuthenticationService authenticationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        authenticationService = new AuthenticationServiceImpl(
            userRepository, rateLimiterService, passwordEncoder, jwtUtil
        );
    }

    @Test
    void testAuthenticate_Success() {
        LoginRequest request = new LoginRequest("testuser", "password123");
        User user = new User("testuser", "test@example.com", "hashedPassword", Role.ADMIN);
        user.setId(1L);
        user.setIsActive(true);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password123", "hashedPassword")).thenReturn(true);
        when(jwtUtil.generateToken(user)).thenReturn("jwt-token");

        AuthResponse response = authenticationService.authenticate(request);

        assertNotNull(response);
        assertEquals("jwt-token", response.getToken());
        assertEquals(900, response.getExpiresIn());
        verify(rateLimiterService).resetFailedAttempts("testuser");
    }

    @Test
    void testAuthenticate_InvalidUsername() {
        LoginRequest request = new LoginRequest("nonexistent", "password123");

        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        assertThrows(AuthenticationException.class, () -> authenticationService.authenticate(request));
        verify(rateLimiterService).recordFailedAttempt("nonexistent");
    }

    @Test
    void testAuthenticate_InvalidPassword() {
        LoginRequest request = new LoginRequest("testuser", "wrongpassword");
        User user = new User("testuser", "test@example.com", "hashedPassword", Role.ADMIN);
        user.setId(1L);
        user.setIsActive(true);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongpassword", "hashedPassword")).thenReturn(false);

        assertThrows(AuthenticationException.class, () -> authenticationService.authenticate(request));
        verify(rateLimiterService).recordFailedAttempt("testuser");
    }

    @Test
    void testAuthenticate_InactiveUser() {
        LoginRequest request = new LoginRequest("testuser", "password123");
        User user = new User("testuser", "test@example.com", "hashedPassword", Role.ADMIN);
        user.setId(1L);
        user.setIsActive(false);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        assertThrows(AuthenticationException.class, () -> authenticationService.authenticate(request));
        verify(rateLimiterService).recordFailedAttempt("testuser");
    }
}
