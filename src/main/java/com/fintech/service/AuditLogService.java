package com.fintech.service;

import com.fintech.model.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AuditLogService {
    /**
     * Retrieves audit logs with filtering and pagination
     */
    Page<AuditLog> getAuditLogs(
        String startDate,
        String endDate,
        Long userId,
        String actionType,
        String entityType,
        Pageable pageable
    );
}
