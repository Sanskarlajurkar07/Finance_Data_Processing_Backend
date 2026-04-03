package com.fintech.service.impl;

import com.fintech.exception.RateLimitException;
import com.fintech.model.RateLimitState;
import com.fintech.service.RateLimiterService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@ConditionalOnMissingBean(RedisTemplate.class)
public class InMemoryRateLimiterServiceImpl implements RateLimiterService {

    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final int WINDOW_MINUTES = 15;
    
    private final Map<String, RateLimitState> rateLimitStore = new ConcurrentHashMap<>();

    @Override
    public void checkRateLimit(String username) {
        RateLimitState state = rateLimitStore.get(username);

        if (state != null && state.getFailedAttempts() >= MAX_FAILED_ATTEMPTS) {
            if (LocalDateTime.now().isBefore(state.getBlockedUntil())) {
                throw new RateLimitException(
                    "Too many failed attempts. Try again after " + state.getBlockedUntil(),
                    state.getBlockedUntil()
                );
            }
        }
    }

    @Override
    public void recordFailedAttempt(String username) {
        RateLimitState state = rateLimitStore.get(username);

        if (state == null) {
            state = new RateLimitState();
            state.setUsername(username);
            state.setFailedAttempts(1);
            state.setWindowStart(LocalDateTime.now());
            state.setBlockedUntil(LocalDateTime.now().plusMinutes(WINDOW_MINUTES));
        } else {
            LocalDateTime now = LocalDateTime.now();
            if (now.isAfter(state.getWindowStart().plusMinutes(WINDOW_MINUTES))) {
                // Window expired, reset
                state.setFailedAttempts(1);
                state.setWindowStart(now);
                state.setBlockedUntil(now.plusMinutes(WINDOW_MINUTES));
            } else {
                // Within window, increment
                state.setFailedAttempts(state.getFailedAttempts() + 1);
                state.setBlockedUntil(now.plusMinutes(WINDOW_MINUTES));
            }
        }

        rateLimitStore.put(username, state);
    }

    @Override
    public void resetFailedAttempts(String username) {
        rateLimitStore.remove(username);
    }

    @Override
    public int getRemainingAttempts(String username) {
        RateLimitState state = rateLimitStore.get(username);

        if (state == null) {
            return MAX_FAILED_ATTEMPTS;
        }

        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(state.getWindowStart().plusMinutes(WINDOW_MINUTES))) {
            // Window expired
            return MAX_FAILED_ATTEMPTS;
        }

        return Math.max(0, MAX_FAILED_ATTEMPTS - state.getFailedAttempts());
    }
}
