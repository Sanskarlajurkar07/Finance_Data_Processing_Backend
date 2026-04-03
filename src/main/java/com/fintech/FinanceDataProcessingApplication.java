package com.fintech;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Main application class for Finance Data Processing Backend.
 * 
 * This application provides:
 * - Secure authentication with JWT tokens
 * - Role-based access control (RBAC)
 * - Financial record management with idempotency
 * - Real-time analytics with Redis caching
 * - Comprehensive audit logging
 */
@SpringBootApplication
@EnableCaching
@EnableAsync
@EnableJpaAuditing
public class FinanceDataProcessingApplication {

    public static void main(String[] args) {
        SpringApplication.run(FinanceDataProcessingApplication.class, args);
    }
}
