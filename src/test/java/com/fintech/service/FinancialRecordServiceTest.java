package com.fintech.service;

import com.fintech.dto.CreateRecordRequest;
import com.fintech.dto.RecordFilter;
import com.fintech.dto.UpdateRecordRequest;
import com.fintech.exception.ConflictException;
import com.fintech.exception.RecordNotFoundException;
import com.fintech.model.FinancialRecord;
import com.fintech.model.RecordType;
import com.fintech.model.Role;
import com.fintech.model.User;
import com.fintech.repository.FinancialRecordRepository;
import com.fintech.repository.UserRepository;
import com.fintech.service.impl.FinancialRecordServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SuppressWarnings("null")
class FinancialRecordServiceTest {

    @Mock
    private FinancialRecordRepository financialRecordRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private DashboardService dashboardService;

    private FinancialRecordService financialRecordService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        financialRecordService = new FinancialRecordServiceImpl(
            financialRecordRepository, userRepository, dashboardService
        );

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
    void testCreateRecord_Success() {
        CreateRecordRequest request = new CreateRecordRequest();
        request.setDescription("Test Income");
        request.setAmount(BigDecimal.valueOf(1000.00));
        request.setType(RecordType.INCOME);
        request.setCategory("Salary");
        request.setRecordDate(LocalDate.now());

        User user = new User("testuser", "test@example.com", "hashedPassword", Role.ADMIN);
        user.setId(1L);

        when(financialRecordRepository.findByIdempotencyKey(null)).thenReturn(Optional.empty());
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(financialRecordRepository.save(any())).thenAnswer(invocation -> {
            FinancialRecord record = invocation.getArgument(0);
            record.setId(1L);
            return record;
        });

        FinancialRecord result = financialRecordService.createRecord(request);

        assertNotNull(result);
        assertEquals("Test Income", result.getDescription());
        assertEquals(BigDecimal.valueOf(1000.00), result.getAmount());
        verify(dashboardService).invalidateCache();
    }

    @Test
    void testCreateRecord_WithIdempotencyKey_Duplicate() {
        CreateRecordRequest request = new CreateRecordRequest();
        request.setDescription("Test Income");
        request.setAmount(BigDecimal.valueOf(1000.00));
        request.setType(RecordType.INCOME);
        request.setCategory("Salary");
        request.setRecordDate(LocalDate.now());
        request.setIdempotencyKey("key123");

        FinancialRecord existingRecord = new FinancialRecord();
        existingRecord.setId(1L);
        existingRecord.setDescription("Different Description");

        when(financialRecordRepository.findByIdempotencyKey("key123")).thenReturn(Optional.of(existingRecord));

        assertThrows(ConflictException.class, () -> financialRecordService.createRecord(request));
    }

    @Test
    void testGetRecordById_Success() {
        FinancialRecord record = new FinancialRecord();
        record.setId(1L);
        record.setDescription("Test Income");

        when(financialRecordRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.of(record));

        FinancialRecord result = financialRecordService.getRecordById(1L);

        assertNotNull(result);
        assertEquals("Test Income", result.getDescription());
    }

    @Test
    void testGetRecordById_NotFound() {
        when(financialRecordRepository.findByIdAndIsActiveTrue(999L)).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class, () -> financialRecordService.getRecordById(999L));
    }

    @Test
    void testGetRecords_NoFilters() {
        FinancialRecord record = new FinancialRecord();
        record.setId(1L);
        Page<FinancialRecord> page = new PageImpl<>(List.of(record));

        when(financialRecordRepository.findAllByIsActiveTrue(any(Pageable.class))).thenReturn(page);

        RecordFilter filter = new RecordFilter(null, null, null, null);
        Pageable pageable = PageRequest.of(0, 20);
        Page<FinancialRecord> result = financialRecordService.getRecords(filter, pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
    }

    @Test
    void testUpdateRecord_Success() {
        FinancialRecord record = new FinancialRecord();
        record.setId(1L);
        record.setDescription("Old Description");
        record.setAmount(BigDecimal.valueOf(500.00));

        UpdateRecordRequest request = new UpdateRecordRequest();
        request.setDescription("New Description");
        request.setAmount(BigDecimal.valueOf(1000.00));
        request.setType(RecordType.INCOME);
        request.setCategory("Salary");
        request.setRecordDate(LocalDate.now());

        when(financialRecordRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.of(record));
        when(financialRecordRepository.save(any())).thenReturn(record);

        FinancialRecord result = financialRecordService.updateRecord(1L, request);

        assertNotNull(result);
        assertEquals("New Description", result.getDescription());
        verify(dashboardService).invalidateCache();
    }

    @Test
    void testDeleteRecord_Success() {
        FinancialRecord record = new FinancialRecord();
        record.setId(1L);
        record.setIsActive(true);

        when(financialRecordRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.of(record));
        when(financialRecordRepository.save(any())).thenReturn(record);

        financialRecordService.deleteRecord(1L);

        assertFalse(record.getIsActive());
        verify(dashboardService).invalidateCache();
    }
}
