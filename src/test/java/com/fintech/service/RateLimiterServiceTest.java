package com.fintech.service;

import com.fintech.exception.RateLimitException;
import com.fintech.service.impl.InMemoryRateLimiterServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("null")
class RateLimiterServiceTest {

    private RateLimiterService rateLimiterService;

    @BeforeEach
    void setUp() {
        rateLimiterService = new InMemoryRateLimiterServiceImpl();
    }

    @Test
    void testCheckRateLimit_NoAttempts() {
        String username = "testuser";
        assertDoesNotThrow(() -> rateLimiterService.checkRateLimit(username));
    }

    @Test
    void testRecordFailedAttempt() {
        String username = "testuser";

        rateLimiterService.recordFailedAttempt(username);

        int remaining = rateLimiterService.getRemainingAttempts(username);
        assertEquals(4, remaining);
    }

    @Test
    void testCheckRateLimit_ExceedsMaxAttempts() {
        String username = "testuser";

        for (int i = 0; i < 5; i++) {
            rateLimiterService.recordFailedAttempt(username);
        }

        assertThrows(RateLimitException.class, () -> rateLimiterService.checkRateLimit(username));
    }

    @Test
    void testResetFailedAttempts() {
        String username = "testuser";

        rateLimiterService.recordFailedAttempt(username);
        rateLimiterService.resetFailedAttempts(username);

        int remaining = rateLimiterService.getRemainingAttempts(username);
        assertEquals(5, remaining);
    }

    @Test
    void testGetRemainingAttempts_NoAttempts() {
        String username = "testuser";
        int remaining = rateLimiterService.getRemainingAttempts(username);
        assertEquals(5, remaining);
    }

    @Test
    void testGetRemainingAttempts_WithAttempts() {
        String username = "testuser";

        rateLimiterService.recordFailedAttempt(username);
        rateLimiterService.recordFailedAttempt(username);

        int remaining = rateLimiterService.getRemainingAttempts(username);
        assertEquals(3, remaining);
    }
}
