package com.fintech.model;

/**
 * ActionType enum defining audit log action types.
 * 
 * - CREATE: Represents creation operations
 * - UPDATE: Represents update operations
 * - DELETE: Represents deletion operations (including soft deletes)
 */
public enum ActionType {
    /**
     * Represents creation operations
     */
    CREATE,
    
    /**
     * Represents update operations
     */
    UPDATE,
    
    /**
     * Represents deletion operations (including soft deletes)
     */
    DELETE
}
