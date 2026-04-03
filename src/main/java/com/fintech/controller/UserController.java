package com.fintech.controller;

import com.fintech.dto.CreateUserRequest;
import com.fintech.dto.UserResponse;
import com.fintech.model.Role;
import com.fintech.model.User;
import com.fintech.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest request) {
        User user = userService.createUser(request);
        return new ResponseEntity<>(mapToResponse(user), HttpStatus.CREATED);
    }

    @GetMapping("/{username}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> getUser(@PathVariable String username) {
        User user = userService.getUserByUsername(username);
        return new ResponseEntity<>(mapToResponse(user), HttpStatus.OK);
    }

    @PutMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deactivateUser(@PathVariable Long id) {
        userService.deactivateUser(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/{id}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> updateUserRole(
        @PathVariable Long id,
        @RequestParam Role role
    ) {
        User user = userService.updateUserRole(id, role);
        return new ResponseEntity<>(mapToResponse(user), HttpStatus.OK);
    }

    private UserResponse mapToResponse(User user) {
        return new UserResponse(
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            user.getRole(),
            user.getIsActive()
        );
    }
}
