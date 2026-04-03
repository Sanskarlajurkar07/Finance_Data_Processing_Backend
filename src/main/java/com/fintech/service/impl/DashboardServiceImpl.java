package com.fintech.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fintech.dto.DashboardAnalytics;
import com.fintech.model.FinancialRecord;
import com.fintech.model.RecordType;
import com.fintech.repository.FinancialRecordRepository;
import com.fintech.service.DashboardService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Implementation of DashboardService with Redis caching and database fallback.
 * 
 * Provides dashboard analytics with:
 * - Redis caching with 5-minute TTL
 * - Automatic cache invalidation on data changes
 * - Database fallback when cache unavailable
 * - BigDecimal precision for financial calculations
 * 
 * Requirements: 8.1, 8.2, 8.3, 8.4, 8.5, 9.1, 9.2, 9.3, 9.4, 9.5, 13.2, 18.1, 18.2, 18.3
 */
@Service
public class DashboardServiceImpl implements DashboardService {
    
    private static final Logger logger = LoggerFactory.getLogger(DashboardServiceImpl.class);
    private static final String CACHE_KEY = "dashboard:analytics";
    
    private final FinancialRecordRepository financialRecordRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;
    private final int cacheTtlSeconds;
    
    public DashboardServiceImpl(
            FinancialRecordRepository financialRecordRepository,
            RedisTemplate<String, String> redisTemplate,
            @Value("${app.cache.dashboard-ttl-seconds:300}") int cacheTtlSeconds) {
        this.financialRecordRepository = financialRecordRepository;
        this.redisTemplate = redisTemplate;
        this.cacheTtlSeconds = cacheTtlSeconds;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }
    
    /**
     * Gets dashboard analytics with caching.
     * 
     * Implementation:
     * 1. Check Redis cache first with key "dashboard:analytics"
     * 2. If cache hit, return cached data
     * 3. If cache miss, calculate from database using calculateAnalyticsFromDatabase()
     * 4. Store result in Redis with 5-minute (300 seconds) TTL
     * 5. Fallback to database-only if Redis unavailable
     * 
     * Requirements: 8.1, 8.5, 9.1, 9.2, 9.3, 9.4
     */
    @Override
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public DashboardAnalytics getAnalytics() {
        try {
            // Try cache first
            String cachedData = redisTemplate.opsForValue().get(CACHE_KEY);
            if (cachedData != null && !cachedData.isEmpty()) {
                logger.debug("Cache hit for dashboard analytics");
                return objectMapper.readValue(cachedData, DashboardAnalytics.class);
            }
            
            logger.debug("Cache miss for dashboard analytics, calculating from database");
            
            // Cache miss - calculate from database
            DashboardAnalytics analytics = calculateAnalyticsFromDatabase();
            
            // Store in cache with TTL
            try {
                String jsonData = objectMapper.writeValueAsString(analytics);
                if (jsonData != null && !jsonData.isEmpty()) {
                    redisTemplate.opsForValue().set(CACHE_KEY, jsonData, cacheTtlSeconds, TimeUnit.SECONDS);
                    logger.debug("Stored analytics in cache with TTL of {} seconds", cacheTtlSeconds);
                }
            } catch (JsonProcessingException e) {
                logger.warn("Failed to serialize analytics for caching, continuing without cache", e);
            }
            
            return analytics;
            
        } catch (Exception e) {
            // Fallback to database-only if Redis unavailable
            logger.warn("Redis unavailable, falling back to database-only calculation", e);
            return calculateAnalyticsFromDatabase();
        }
    }
    
    /**
     * Calculates analytics from database.
     * 
     * Implementation:
     * 1. Retrieve all active financial records (isActive = true)
     * 2. Calculate totalIncome (sum of INCOME records) using BigDecimal arithmetic
     * 3. Calculate totalExpenses (sum of EXPENSE records) using BigDecimal arithmetic
     * 4. Calculate netBalance (totalIncome - totalExpenses)
     * 5. Build category breakdown map with BigDecimal sums per category
     * 6. Return DashboardAnalytics with calculatedAt timestamp
     * 
     * Requirements: 8.2, 8.3, 8.4, 13.2
     */
    private DashboardAnalytics calculateAnalyticsFromDatabase() {
        logger.debug("Calculating analytics from database");
        
        // Retrieve all active financial records
        List<FinancialRecord> records = financialRecordRepository.findAllByIsActiveTrue();
        
        // Initialize accumulators with BigDecimal.ZERO
        BigDecimal totalIncome = BigDecimal.ZERO;
        BigDecimal totalExpenses = BigDecimal.ZERO;
        Map<String, BigDecimal> categoryBreakdown = new HashMap<>();
        
        // Process each record
        for (FinancialRecord record : records) {
            BigDecimal amount = record.getAmount();
            String category = record.getCategory();
            
            // Calculate totals by type
            if (record.getType() == RecordType.INCOME) {
                totalIncome = totalIncome.add(amount);
            } else if (record.getType() == RecordType.EXPENSE) {
                totalExpenses = totalExpenses.add(amount);
            }
            
            // Build category breakdown
            categoryBreakdown.merge(category, amount, BigDecimal::add);
        }
        
        // Calculate net balance
        BigDecimal netBalance = totalIncome.subtract(totalExpenses);
        
        logger.debug("Analytics calculated: totalIncome={}, totalExpenses={}, netBalance={}, categories={}",
                totalIncome, totalExpenses, netBalance, categoryBreakdown.size());
        
        return new DashboardAnalytics(
                totalIncome,
                totalExpenses,
                netBalance,
                categoryBreakdown,
                LocalDateTime.now()
        );
    }
    
    /**
     * Invalidates dashboard cache.
     * 
     * Implementation:
     * 1. Delete "dashboard:analytics" key from Redis
     * 2. Handle Redis unavailability gracefully (log warning, don't fail)
     * 3. Called after any financial record write operation
     * 
     * Requirements: 9.5, 18.1, 18.2, 18.3
     */
    @Override
    public void invalidateCache() {
        try {
            Boolean deleted = redisTemplate.delete(CACHE_KEY);
            if (Boolean.TRUE.equals(deleted)) {
                logger.debug("Dashboard cache invalidated successfully");
            } else {
                logger.debug("Dashboard cache key did not exist, nothing to invalidate");
            }
        } catch (Exception e) {
            // Handle Redis unavailability gracefully - log warning but don't fail
            logger.warn("Failed to invalidate dashboard cache, will expire naturally after {} seconds", 
                    cacheTtlSeconds, e);
        }
    }
}
