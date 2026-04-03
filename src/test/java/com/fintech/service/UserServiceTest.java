package com.fintech.service;

import com.fintech.dto.CreateUserRequest;
import com.fintech.exception.ConflictException;
import com.fintech.exception.RecordNotFoundException;
import com.fintech.model.Role;
import com.fintech.model.User;
import com.fintech.repository.UserRepository;
import com.fintech.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SuppressWarnings("null")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userService = new UserServiceImpl(userRepository, passwordEncoder);
    }

    @Test
    void testCreateUser_Success() {
        CreateUserRequest request = new CreateUserRequest("newuser", "new@example.com", "password123", Role.ANALYST);

        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(1L);
            return user;
        });

        User result = userService.createUser(request);

        assertNotNull(result);
        assertEquals("newuser", result.getUsername());
        assertEquals("new@example.com", result.getEmail());
        assertEquals(Role.ANALYST, result.getRole());
        assertTrue(result.getIsActive());
    }

    @Test
    void testCreateUser_DuplicateUsername() {
        CreateUserRequest request = new CreateUserRequest("existinguser", "new@example.com", "password123", Role.ANALYST);

        when(userRepository.existsByUsername("existinguser")).thenReturn(true);

        assertThrows(ConflictException.class, () -> userService.createUser(request));
    }

    @Test
    void testCreateUser_DuplicateEmail() {
        CreateUserRequest request = new CreateUserRequest("newuser", "existing@example.com", "password123", Role.ANALYST);

        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

        assertThrows(ConflictException.class, () -> userService.createUser(request));
    }

    @Test
    void testCreateUser_PasswordTooShort() {
        CreateUserRequest request = new CreateUserRequest("newuser", "new@example.com", "short", Role.ANALYST);

        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> userService.createUser(request));
    }

    @Test
    void testGetUserByUsername_Success() {
        User user = new User("testuser", "test@example.com", "hashedPassword", Role.ADMIN);
        user.setId(1L);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        User result = userService.getUserByUsername("testuser");

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
    }

    @Test
    void testGetUserByUsername_NotFound() {
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class, () -> userService.getUserByUsername("nonexistent"));
    }

    @Test
    void testDeactivateUser_Success() {
        User user = new User("testuser", "test@example.com", "hashedPassword", Role.ADMIN);
        user.setId(1L);
        user.setIsActive(true);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        userService.deactivateUser(1L);

        assertFalse(user.getIsActive());
        verify(userRepository).save(user);
    }

    @Test
    void testUpdateUserRole_Success() {
        User user = new User("testuser", "test@example.com", "hashedPassword", Role.VIEWER);
        user.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = userService.updateUserRole(1L, Role.ANALYST);

        assertEquals(Role.ANALYST, result.getRole());
        verify(userRepository).save(user);
    }
}
