package com.fintech.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class RateLimitState implements Serializable {
    private static final long serialVersionUID = 1L;

    private String username;
    private int failedAttempts;
    private LocalDateTime windowStart;
    private LocalDateTime blockedUntil;

    public RateLimitState() {
    }

    public RateLimitState(String username, int failedAttempts, LocalDateTime windowStart, LocalDateTime blockedUntil) {
        this.username = username;
        this.failedAttempts = failedAttempts;
        this.windowStart = windowStart;
        this.blockedUntil = blockedUntil;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getFailedAttempts() {
        return failedAttempts;
    }

    public void setFailedAttempts(int failedAttempts) {
        this.failedAttempts = failedAttempts;
    }

    public LocalDateTime getWindowStart() {
        return windowStart;
    }

    public void setWindowStart(LocalDateTime windowStart) {
        this.windowStart = windowStart;
    }

    public LocalDateTime getBlockedUntil() {
        return blockedUntil;
    }

    public void setBlockedUntil(LocalDateTime blockedUntil) {
        this.blockedUntil = blockedUntil;
    }
}
