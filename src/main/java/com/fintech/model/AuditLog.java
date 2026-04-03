package com.fintech.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * AuditLog entity representing immutable audit trail entries.
 * 
 * Records all write operations (create, update, delete) on financial records
 * and user accounts. All fields are final and immutable after construction.
 * No setters provided to enforce immutability at the application level.
 * Database triggers prevent updates and deletes at the database level.
 * 
 * Requirements: 10.1, 10.2, 10.3, 10.5
 */
@Entity
@Table(name = "audit_logs", indexes = {
    @Index(name = "idx_timestamp", columnList = "timestamp"),
    @Index(name = "idx_user_id", columnList = "userId"),
    @Index(name = "idx_action_type", columnList = "actionType"),
    @Index(name = "idx_entity_type", columnList = "entityType")
})
public class AuditLog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private final Long id;
    
    @NotNull(message = "Action type is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20, name = "action_type")
    private final ActionType actionType;
    
    @NotNull(message = "Entity type is required")
    @Size(min = 1, max = 50, message = "Entity type must be between 1 and 50 characters")
    @Column(nullable = false, length = 50, name = "entity_type")
    private final String entityType;
    
    @NotNull(message = "Entity ID is required")
    @Column(nullable = false, name = "entity_id")
    private final Long entityId;
    
    @NotNull(message = "User ID is required")
    @Column(nullable = false, name = "user_id")
    private final Long userId;
    
    @NotNull(message = "Timestamp is required")
    @Column(nullable = false, updatable = false)
    private final LocalDateTime timestamp;
    
    @Column(columnDefinition = "TEXT")
    private final String details;
    
    /**
     * Default constructor for JPA.
     * Required by JPA specification but should not be used directly.
     */
    protected AuditLog() {
        this.id = null;
        this.actionType = null;
        this.entityType = null;
        this.entityId = null;
        this.userId = null;
        this.timestamp = null;
        this.details = null;
    }
    
    /**
     * Constructor for creating a new audit log entry.
     * All fields are set at construction time and cannot be modified.
     * 
     * @param actionType the type of action performed (CREATE, UPDATE, DELETE)
     * @param entityType the type of entity affected (e.g., "FinancialRecord", "User")
     * @param entityId the ID of the affected entity
     * @param userId the ID of the user who performed the action
     * @param timestamp the timestamp when the action occurred
     * @param details JSON serialized details of the operation (optional)
     */
    public AuditLog(ActionType actionType, String entityType, Long entityId, 
                    Long userId, LocalDateTime timestamp, String details) {
        this.id = null; // Will be set by database
        this.actionType = actionType;
        this.entityType = entityType;
        this.entityId = entityId;
        this.userId = userId;
        this.timestamp = timestamp;
        this.details = details;
    }
    
    /**
     * Convenience constructor without details field.
     * 
     * @param actionType the type of action performed (CREATE, UPDATE, DELETE)
     * @param entityType the type of entity affected (e.g., "FinancialRecord", "User")
     * @param entityId the ID of the affected entity
     * @param userId the ID of the user who performed the action
     * @param timestamp the timestamp when the action occurred
     */
    public AuditLog(ActionType actionType, String entityType, Long entityId, 
                    Long userId, LocalDateTime timestamp) {
        this(actionType, entityType, entityId, userId, timestamp, null);
    }
    
    // Getters only - no setters to enforce immutability
    
    public Long getId() {
        return id;
    }
    
    public ActionType getActionType() {
        return actionType;
    }
    
    public String getEntityType() {
        return entityType;
    }
    
    public Long getEntityId() {
        return entityId;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public String getDetails() {
        return details;
    }
    
    /**
     * Equals based on id for entity identity.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AuditLog auditLog = (AuditLog) o;
        return Objects.equals(id, auditLog.id);
    }
    
    /**
     * HashCode based on id for entity identity.
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    /**
     * String representation for debugging.
     * Does not include details field to avoid logging sensitive information.
     */
    @Override
    public String toString() {
        return "AuditLog{" +
                "id=" + id +
                ", actionType=" + actionType +
                ", entityType='" + entityType + '\'' +
                ", entityId=" + entityId +
                ", userId=" + userId +
                ", timestamp=" + timestamp +
                '}';
    }
}
