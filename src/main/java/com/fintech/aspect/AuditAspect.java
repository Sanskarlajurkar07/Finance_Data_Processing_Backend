package com.fintech.aspect;

import com.fintech.annotation.Auditable;
import com.fintech.model.ActionType;
import com.fintech.model.AuditLog;
import com.fintech.model.FinancialRecord;
import com.fintech.model.User;
import com.fintech.repository.AuditLogRepository;
import com.fintech.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Aspect
@Component
public class AuditAspect {

    private final AuditLogRepository auditLogRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    public AuditAspect(AuditLogRepository auditLogRepository, UserRepository userRepository, 
                       ObjectMapper objectMapper) {
        this.auditLogRepository = auditLogRepository;
        this.userRepository = userRepository;
        this.objectMapper = objectMapper;
    }

    @AfterReturning(pointcut = "@annotation(auditable)", returning = "result")
    public void logAuditEvent(JoinPoint joinPoint, Auditable auditable, Object result) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return;
            }

            String username = authentication.getName();
            User user = userRepository.findByUsername(username).orElse(null);
            if (user == null) {
                return;
            }

            Long userId = user.getId();
            ActionType actionType = auditable.actionType();
            String entityType = extractEntityType(result);
            Long entityId = extractEntityId(result);

            if (entityType == null || entityId == null) {
                return;
            }

            String details = objectMapper.writeValueAsString(result);

            AuditLog auditLog = new AuditLog(
                actionType,
                entityType,
                entityId,
                userId,
                LocalDateTime.now(),
                details
            );

            auditLogRepository.save(auditLog);
        } catch (Exception e) {
            // Log error but don't fail the operation
            System.err.println("Failed to create audit log: " + e.getMessage());
        }
    }

    private String extractEntityType(Object result) {
        if (result instanceof FinancialRecord) {
            return "FinancialRecord";
        } else if (result instanceof User) {
            return "User";
        }
        return null;
    }

    private Long extractEntityId(Object result) {
        if (result instanceof FinancialRecord) {
            return ((FinancialRecord) result).getId();
        } else if (result instanceof User) {
            return ((User) result).getId();
        }
        return null;
    }
}
