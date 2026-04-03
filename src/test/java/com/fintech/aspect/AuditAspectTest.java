package com.fintech.aspect;

import com.fintech.annotation.Auditable;
import com.fintech.model.ActionType;
import com.fintech.model.AuditLog;
import com.fintech.model.FinancialRecord;
import com.fintech.model.Role;
import com.fintech.model.User;
import com.fintech.repository.AuditLogRepository;
import com.fintech.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SuppressWarnings("null")
class AuditAspectTest {

    @Mock
    private AuditLogRepository auditLogRepository;

    @Mock
    private UserRepository userRepository;

    private AuditAspect auditAspect;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ObjectMapper objectMapper = new ObjectMapper();
        auditAspect = new AuditAspect(auditLogRepository, userRepository, objectMapper);

        // Setup security context
        User user = new User("testuser", "test@example.com", "hashedPassword", Role.ADMIN);
        user.setId(1L);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        // Create authenticated token (3-arg constructor marks it as authenticated)
        var auth = new UsernamePasswordAuthenticationToken("testuser", null, 
            java.util.Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    void testAuditLogging_FinancialRecordCreation() {
        FinancialRecord record = new FinancialRecord();
        record.setId(1L);
        record.setDescription("Test Income");
        record.setAmount(BigDecimal.valueOf(1000.00));

        Auditable auditable = new Auditable() {
            @Override
            public ActionType actionType() {
                return ActionType.CREATE;
            }

            @Override
            public Class<? extends java.lang.annotation.Annotation> annotationType() {
                return Auditable.class;
            }
        };

        auditAspect.logAuditEvent(null, auditable, record);

        ArgumentCaptor<AuditLog> captor = ArgumentCaptor.forClass(AuditLog.class);
        verify(auditLogRepository).save(captor.capture());

        AuditLog savedLog = captor.getValue();
        assertNotNull(savedLog);
        assertEquals(ActionType.CREATE, savedLog.getActionType());
        assertEquals("FinancialRecord", savedLog.getEntityType());
        assertEquals(1L, savedLog.getEntityId());
        assertEquals(1L, savedLog.getUserId());
    }

    @Test
    void testAuditLogging_UserCreation() {
        User user = new User("newuser", "new@example.com", "hashedPassword", Role.ANALYST);
        user.setId(2L);

        Auditable auditable = new Auditable() {
            @Override
            public ActionType actionType() {
                return ActionType.CREATE;
            }

            @Override
            public Class<? extends java.lang.annotation.Annotation> annotationType() {
                return Auditable.class;
            }
        };

        auditAspect.logAuditEvent(null, auditable, user);

        ArgumentCaptor<AuditLog> captor = ArgumentCaptor.forClass(AuditLog.class);
        verify(auditLogRepository).save(captor.capture());

        AuditLog savedLog = captor.getValue();
        assertNotNull(savedLog);
        assertEquals(ActionType.CREATE, savedLog.getActionType());
        assertEquals("User", savedLog.getEntityType());
        assertEquals(2L, savedLog.getEntityId());
    }

    @Test
    void testAuditLogging_NoAuthentication() {
        SecurityContextHolder.clearContext();

        FinancialRecord record = new FinancialRecord();
        record.setId(1L);

        Auditable auditable = new Auditable() {
            @Override
            public ActionType actionType() {
                return ActionType.CREATE;
            }

            @Override
            public Class<? extends java.lang.annotation.Annotation> annotationType() {
                return Auditable.class;
            }
        };

        auditAspect.logAuditEvent(null, auditable, record);

        verify(auditLogRepository, never()).save(any());
    }
}
