package com.fintech.exception;

/**
 * Exception thrown when a financial record is not found or is inactive.
 * 
 * Validates: Requirements 5.4, 6.5, 7.5
 */
public class RecordNotFoundException extends RuntimeException {
    
    public RecordNotFoundException(String message) {
        super(message);
    }
    
    public RecordNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public RecordNotFoundException(Long recordId) {
        super("Financial record not found with ID: " + recordId);
    }
}
