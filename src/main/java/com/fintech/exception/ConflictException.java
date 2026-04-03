package com.fintech.exception;

/**
 * Exception thrown when an idempotency key is reused with different data.
 * 
 * Validates: Requirements 12.5
 */
public class ConflictException extends RuntimeException {
    
    public ConflictException(String message) {
        super(message);
    }
    
    public ConflictException(String message, Throwable cause) {
        super(message, cause);
    }
}
