package com.fintech.service;

import com.fintech.dto.CreateUserRequest;
import com.fintech.model.Role;
import com.fintech.model.User;

public interface UserService {
    /**
     * Creates new user account (ADMIN only)
     * @throws com.fintech.exception.ConflictException if username or email exists
     */
    User createUser(CreateUserRequest request);

    /**
     * Retrieves user by username
     * @throws com.fintech.exception.RecordNotFoundException if not found
     */
    User getUserByUsername(String username);

    /**
     * Deactivates user account (ADMIN only)
     */
    void deactivateUser(Long userId);

    /**
     * Updates user role (ADMIN only)
     */
    User updateUserRole(Long userId, Role newRole);
}
