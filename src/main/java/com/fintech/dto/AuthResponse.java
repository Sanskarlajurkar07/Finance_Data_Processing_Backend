package com.fintech.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * DTO for authentication response.
 * Contains JWT token and expiration time in seconds.
 * 
 * Validates: Requirements 1.1
 */
public class AuthResponse {
    
    @NotBlank(message = "Token is required")
    private String token;
    
    @NotNull(message = "ExpiresIn is required")
    @Positive(message = "ExpiresIn must be positive")
    private Integer expiresIn;
    
    public AuthResponse() {
    }
    
    public AuthResponse(String token, Integer expiresIn) {
        this.token = token;
        this.expiresIn = expiresIn;
    }
    
    public String getToken() {
        return token;
    }
    
    public void setToken(String token) {
        this.token = token;
    }
    
    public Integer getExpiresIn() {
        return expiresIn;
    }
    
    public void setExpiresIn(Integer expiresIn) {
        this.expiresIn = expiresIn;
    }
}
