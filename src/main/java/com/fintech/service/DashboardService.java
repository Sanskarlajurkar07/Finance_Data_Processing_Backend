package com.fintech.service;

import com.fintech.dto.DashboardAnalytics;

/**
 * Service interface for dashboard analytics operations.
 * 
 * Provides aggregated financial analytics with Redis caching support,
 * automatic cache invalidation, and database fallback for high availability.
 * 
 * Requirements: 8.1, 8.5, 9.1, 9.2, 9.3, 9.4, 9.5, 18.1, 18.2, 18.3
 */
public interface DashboardService {
    
    /**
     * Gets dashboard analytics with caching.
     * 
     * Checks Redis cache first with key "dashboard:analytics".
     * If cache hit, returns cached data.
     * If cache miss, calculates from database and stores in cache with 5-minute TTL.
     * Falls back to database-only if Redis unavailable.
     * 
     * @return dashboard analytics with aggregated financial metrics
     */
    DashboardAnalytics getAnalytics();
    
    /**
     * Invalidates dashboard cache.
     * 
     * Deletes "dashboard:analytics" key from Redis.
     * Handles Redis unavailability gracefully (logs warning, doesn't fail).
     * Should be called after any financial record write operation.
     */
    void invalidateCache();
}
