package com.fintech.repository;

import com.fintech.model.ActionType;
import com.fintech.model.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Spring Data JPA repository for AuditLog entity.
 * 
 * Provides read-only operations for audit log retrieval with filtering and pagination.
 * Supports filtering by date range, userId, actionType, and entityType.
 * Default ordering is by timestamp descending (newest first).
 * 
 * Note: This repository only supports read operations. Audit logs are created
 * by the AuditAspect and are immutable after creation.
 * 
 * Requirements: 19.1, 19.2, 19.4
 */
@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    
    /**
     * Finds all audit logs ordered by timestamp descending (newest first).
     * This is the default ordering for audit log queries.
     * 
     * @param pageable pagination parameters
     * @return page of audit logs ordered by timestamp DESC
     */
    Page<AuditLog> findAllByOrderByTimestampDesc(Pageable pageable);
    
    /**
     * Finds audit logs within a date range, ordered by timestamp descending.
     * 
     * @param startDate the start date/time (inclusive)
     * @param endDate the end date/time (inclusive)
     * @param pageable pagination parameters
     * @return page of audit logs within the date range
     */
    Page<AuditLog> findByTimestampBetweenOrderByTimestampDesc(
        LocalDateTime startDate,
        LocalDateTime endDate,
        Pageable pageable
    );
    
    /**
     * Finds audit logs for a specific user, ordered by timestamp descending.
     * 
     * @param userId the ID of the user who performed the actions
     * @param pageable pagination parameters
     * @return page of audit logs for the specified user
     */
    Page<AuditLog> findByUserIdOrderByTimestampDesc(Long userId, Pageable pageable);
    
    /**
     * Finds audit logs for a specific action type, ordered by timestamp descending.
     * 
     * @param actionType the action type (CREATE, UPDATE, DELETE)
     * @param pageable pagination parameters
     * @return page of audit logs for the specified action type
     */
    Page<AuditLog> findByActionTypeOrderByTimestampDesc(ActionType actionType, Pageable pageable);
    
    /**
     * Finds audit logs for a specific entity type, ordered by timestamp descending.
     * 
     * @param entityType the entity type (e.g., "FinancialRecord", "User")
     * @param pageable pagination parameters
     * @return page of audit logs for the specified entity type
     */
    Page<AuditLog> findByEntityTypeOrderByTimestampDesc(String entityType, Pageable pageable);
    
    /**
     * Finds audit logs filtered by date range and user ID.
     * 
     * @param startDate the start date/time (inclusive)
     * @param endDate the end date/time (inclusive)
     * @param userId the user ID
     * @param pageable pagination parameters
     * @return page of audit logs matching the filters
     */
    Page<AuditLog> findByTimestampBetweenAndUserIdOrderByTimestampDesc(
        LocalDateTime startDate,
        LocalDateTime endDate,
        Long userId,
        Pageable pageable
    );
    
    /**
     * Finds audit logs filtered by date range and action type.
     * 
     * @param startDate the start date/time (inclusive)
     * @param endDate the end date/time (inclusive)
     * @param actionType the action type
     * @param pageable pagination parameters
     * @return page of audit logs matching the filters
     */
    Page<AuditLog> findByTimestampBetweenAndActionTypeOrderByTimestampDesc(
        LocalDateTime startDate,
        LocalDateTime endDate,
        ActionType actionType,
        Pageable pageable
    );
    
    /**
     * Finds audit logs filtered by date range and entity type.
     * 
     * @param startDate the start date/time (inclusive)
     * @param endDate the end date/time (inclusive)
     * @param entityType the entity type
     * @param pageable pagination parameters
     * @return page of audit logs matching the filters
     */
    Page<AuditLog> findByTimestampBetweenAndEntityTypeOrderByTimestampDesc(
        LocalDateTime startDate,
        LocalDateTime endDate,
        String entityType,
        Pageable pageable
    );
    
    /**
     * Finds audit logs filtered by user ID and action type.
     * 
     * @param userId the user ID
     * @param actionType the action type
     * @param pageable pagination parameters
     * @return page of audit logs matching the filters
     */
    Page<AuditLog> findByUserIdAndActionTypeOrderByTimestampDesc(
        Long userId,
        ActionType actionType,
        Pageable pageable
    );
    
    /**
     * Finds audit logs filtered by user ID and entity type.
     * 
     * @param userId the user ID
     * @param entityType the entity type
     * @param pageable pagination parameters
     * @return page of audit logs matching the filters
     */
    Page<AuditLog> findByUserIdAndEntityTypeOrderByTimestampDesc(
        Long userId,
        String entityType,
        Pageable pageable
    );
    
    /**
     * Finds audit logs filtered by action type and entity type.
     * 
     * @param actionType the action type
     * @param entityType the entity type
     * @param pageable pagination parameters
     * @return page of audit logs matching the filters
     */
    Page<AuditLog> findByActionTypeAndEntityTypeOrderByTimestampDesc(
        ActionType actionType,
        String entityType,
        Pageable pageable
    );
    
    /**
     * Finds audit logs filtered by date range, user ID, and action type.
     * 
     * @param startDate the start date/time (inclusive)
     * @param endDate the end date/time (inclusive)
     * @param userId the user ID
     * @param actionType the action type
     * @param pageable pagination parameters
     * @return page of audit logs matching all filters
     */
    Page<AuditLog> findByTimestampBetweenAndUserIdAndActionTypeOrderByTimestampDesc(
        LocalDateTime startDate,
        LocalDateTime endDate,
        Long userId,
        ActionType actionType,
        Pageable pageable
    );
    
    /**
     * Finds audit logs filtered by date range, user ID, and entity type.
     * 
     * @param startDate the start date/time (inclusive)
     * @param endDate the end date/time (inclusive)
     * @param userId the user ID
     * @param entityType the entity type
     * @param pageable pagination parameters
     * @return page of audit logs matching all filters
     */
    Page<AuditLog> findByTimestampBetweenAndUserIdAndEntityTypeOrderByTimestampDesc(
        LocalDateTime startDate,
        LocalDateTime endDate,
        Long userId,
        String entityType,
        Pageable pageable
    );
    
    /**
     * Finds audit logs filtered by date range, action type, and entity type.
     * 
     * @param startDate the start date/time (inclusive)
     * @param endDate the end date/time (inclusive)
     * @param actionType the action type
     * @param entityType the entity type
     * @param pageable pagination parameters
     * @return page of audit logs matching all filters
     */
    Page<AuditLog> findByTimestampBetweenAndActionTypeAndEntityTypeOrderByTimestampDesc(
        LocalDateTime startDate,
        LocalDateTime endDate,
        ActionType actionType,
        String entityType,
        Pageable pageable
    );
    
    /**
     * Finds audit logs filtered by user ID, action type, and entity type.
     * 
     * @param userId the user ID
     * @param actionType the action type
     * @param entityType the entity type
     * @param pageable pagination parameters
     * @return page of audit logs matching all filters
     */
    Page<AuditLog> findByUserIdAndActionTypeAndEntityTypeOrderByTimestampDesc(
        Long userId,
        ActionType actionType,
        String entityType,
        Pageable pageable
    );
    
    /**
     * Finds audit logs filtered by all criteria: date range, user ID, action type, and entity type.
     * Supports comprehensive filtering for audit log queries.
     * 
     * @param startDate the start date/time (inclusive)
     * @param endDate the end date/time (inclusive)
     * @param userId the user ID
     * @param actionType the action type
     * @param entityType the entity type
     * @param pageable pagination parameters
     * @return page of audit logs matching all filters
     */
    Page<AuditLog> findByTimestampBetweenAndUserIdAndActionTypeAndEntityTypeOrderByTimestampDesc(
        LocalDateTime startDate,
        LocalDateTime endDate,
        Long userId,
        ActionType actionType,
        String entityType,
        Pageable pageable
    );
    
    /**
     * Custom query to find audit logs for a specific entity.
     * Useful for retrieving the complete audit trail of a single entity.
     * 
     * @param entityType the entity type
     * @param entityId the entity ID
     * @param pageable pagination parameters
     * @return page of audit logs for the specified entity, ordered by timestamp DESC
     */
    @Query("SELECT al FROM AuditLog al WHERE al.entityType = :entityType AND al.entityId = :entityId ORDER BY al.timestamp DESC")
    Page<AuditLog> findByEntityTypeAndEntityId(
        @Param("entityType") String entityType,
        @Param("entityId") Long entityId,
        Pageable pageable
    );
    
    /**
     * Custom query to find recent audit logs (last N entries).
     * Optimized for dashboard or monitoring displays.
     * 
     * @param pageable pagination parameters (use PageRequest.of(0, limit) for top N)
     * @return page of most recent audit logs
     */
    @Query("SELECT al FROM AuditLog al ORDER BY al.timestamp DESC")
    Page<AuditLog> findRecentAuditLogs(Pageable pageable);
    
    /**
     * Finds all audit logs for a specific entity (without pagination).
     * Useful for complete audit trail retrieval.
     * 
     * @param entityType the entity type
     * @param entityId the entity ID
     * @return list of all audit logs for the entity, ordered by timestamp DESC
     */
    @Query("SELECT al FROM AuditLog al WHERE al.entityType = :entityType AND al.entityId = :entityId ORDER BY al.timestamp DESC")
    List<AuditLog> findAllByEntityTypeAndEntityId(
        @Param("entityType") String entityType,
        @Param("entityId") Long entityId
    );

    /**
     * Dynamic query to find audit logs with flexible filtering.
     * Handles null parameters gracefully.
     * 
     * @param startDate optional start date
     * @param endDate optional end date
     * @param userId optional user ID
     * @param actionType optional action type string
     * @param entityType optional entity type
     * @param pageable pagination parameters
     * @return page of audit logs matching the filters
     */
    @Query("SELECT al FROM AuditLog al WHERE " +
           "(:startDate IS NULL OR al.timestamp >= :startDate) AND " +
           "(:endDate IS NULL OR al.timestamp <= :endDate) AND " +
           "(:userId IS NULL OR al.userId = :userId) AND " +
           "(:actionType IS NULL OR CAST(al.actionType AS string) = :actionType) AND " +
           "(:entityType IS NULL OR al.entityType = :entityType) " +
           "ORDER BY al.timestamp DESC")
    Page<AuditLog> findByFilters(
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate,
        @Param("userId") Long userId,
        @Param("actionType") String actionType,
        @Param("entityType") String entityType,
        Pageable pageable
    );
}
