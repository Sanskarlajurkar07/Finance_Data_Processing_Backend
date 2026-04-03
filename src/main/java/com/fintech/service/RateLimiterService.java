package com.fintech.service;

public interface RateLimiterService {
    /**
     * Checks if user has exceeded rate limit
     * @throws com.fintech.exception.RateLimitException if limit exceeded
     */
    void checkRateLimit(String username);

    /**
     * Records failed authentication attempt
     */
    void recordFailedAttempt(String username);

    /**
     * Resets failed attempt counter
     */
    void resetFailedAttempts(String username);

    /**
     * Gets remaining attempts before rate limit
     */
    int getRemainingAttempts(String username);
}
