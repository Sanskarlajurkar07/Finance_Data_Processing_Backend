package com.fintech.service.impl;

import com.fintech.model.AuditLog;
import com.fintech.repository.AuditLogRepository;
import com.fintech.service.AuditLogService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class AuditLogServiceImpl implements AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public AuditLogServiceImpl(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @Override
    public Page<AuditLog> getAuditLogs(
        String startDate,
        String endDate,
        Long userId,
        String actionType,
        String entityType,
        Pageable pageable
    ) {
        LocalDateTime start = null;
        LocalDateTime end = null;

        if (startDate != null && !startDate.isEmpty()) {
            start = LocalDateTime.parse(startDate, DateTimeFormatter.ISO_DATE_TIME);
        }

        if (endDate != null && !endDate.isEmpty()) {
            end = LocalDateTime.parse(endDate, DateTimeFormatter.ISO_DATE_TIME);
        }

        return auditLogRepository.findByFilters(start, end, userId, actionType, entityType, pageable);
    }
}
