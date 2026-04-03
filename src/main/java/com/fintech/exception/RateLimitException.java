package com.fintech.exception;

import java.time.LocalDateTime;

public class RateLimitException extends RuntimeException {
    private final LocalDateTime retryAfter;

    public RateLimitException(String message, LocalDateTime retryAfter) {
        super(message);
        this.retryAfter = retryAfter;
    }

    public LocalDateTime getRetryAfter() {
        return retryAfter;
    }
}
