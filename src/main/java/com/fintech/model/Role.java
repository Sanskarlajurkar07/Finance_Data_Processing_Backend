package com.fintech.model;

/**
 * Role enum defining user access levels in the system.
 * 
 * - ADMIN: Full access to all operations
 * - ANALYST: Read access to records and dashboard
 * - VIEWER: Read-only access to records
 */
public enum Role {
    /**
     * Full access to all operations including create, read, update, and delete
     */
    ADMIN,
    
    /**
     * Read access to financial records and dashboard analytics
     */
    ANALYST,
    
    /**
     * Read-only access to financial records
     */
    VIEWER
}
