package com.fintech.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for consistent error responses across the API.
 * Provides structured error information including timestamp, status code,
 * error type, message, field-specific errors, and request path.
 * 
 * Validates: Requirements 16.1, 16.2, 16.3, 16.4, 16.5
 */
public class ErrorResponse {
    
    private LocalDateTime timestamp;
    private Integer status;
    private String error;
    private String message;
    private List<FieldError> errors;
    private String path;
    
    public ErrorResponse() {
    }
    
    public ErrorResponse(LocalDateTime timestamp, Integer status, String error, 
                        String message, List<FieldError> errors, String path) {
        this.timestamp = timestamp;
        this.status = status;
        this.error = error;
        this.message = message;
        this.errors = errors;
        this.path = path;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    
    public Integer getStatus() {
        return status;
    }
    
    public void setStatus(Integer status) {
        this.status = status;
    }
    
    public String getError() {
        return error;
    }
    
    public void setError(String error) {
        this.error = error;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public List<FieldError> getErrors() {
        return errors;
    }
    
    public void setErrors(List<FieldError> errors) {
        this.errors = errors;
    }
    
    public String getPath() {
        return path;
    }
    
    public void setPath(String path) {
        this.path = path;
    }
}
