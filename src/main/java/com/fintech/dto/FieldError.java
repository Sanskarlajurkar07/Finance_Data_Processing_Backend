package com.fintech.dto;

/**
 * DTO for field-specific validation errors.
 * Used within ErrorResponse to provide detailed validation error information.
 * 
 * Validates: Requirements 16.1
 */
public class FieldError {
    
    private String field;
    private String message;
    
    public FieldError() {
    }
    
    public FieldError(String field, String message) {
        this.field = field;
        this.message = message;
    }
    
    public String getField() {
        return field;
    }
    
    public void setField(String field) {
        this.field = field;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
}
