package com.fintech.service;

import com.fintech.dto.DashboardAnalytics;
import com.fintech.model.FinancialRecord;
import com.fintech.model.RecordType;
import com.fintech.repository.FinancialRecordRepository;
import com.fintech.service.impl.DashboardServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SuppressWarnings("null")
class DashboardServiceTest {

    @Mock
    private FinancialRecordRepository financialRecordRepository;

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    private DashboardService dashboardService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        dashboardService = new DashboardServiceImpl(financialRecordRepository, redisTemplate, 300);
    }

    @Test
    void testGetAnalytics_CacheHit() {
        String cachedJson = "{\"totalIncome\":5000.00,\"totalExpenses\":2000.00,\"netBalance\":3000.00}";
        when(valueOperations.get("dashboard:analytics")).thenReturn(cachedJson);

        DashboardAnalytics result = dashboardService.getAnalytics();

        assertNotNull(result);
        verify(financialRecordRepository, never()).findAllByIsActiveTrue();
    }

    @Test
    void testGetAnalytics_CacheMiss() {
        FinancialRecord record1 = new FinancialRecord();
        record1.setId(1L);
        record1.setAmount(BigDecimal.valueOf(1000.00));
        record1.setType(RecordType.INCOME);
        record1.setCategory("Salary");

        FinancialRecord record2 = new FinancialRecord();
        record2.setId(2L);
        record2.setAmount(BigDecimal.valueOf(500.00));
        record2.setType(RecordType.EXPENSE);
        record2.setCategory("Food");

        when(valueOperations.get("dashboard:analytics")).thenReturn(null);
        when(financialRecordRepository.findAllByIsActiveTrue()).thenReturn(List.of(record1, record2));
        doNothing().when(valueOperations).set(eq("dashboard:analytics"), anyString(), eq(300L), eq(TimeUnit.SECONDS));

        DashboardAnalytics result = dashboardService.getAnalytics();

        assertNotNull(result);
        assertEquals(BigDecimal.valueOf(1000.00), result.getTotalIncome());
        assertEquals(BigDecimal.valueOf(500.00), result.getTotalExpenses());
        assertEquals(BigDecimal.valueOf(500.00), result.getNetBalance());
        verify(valueOperations).set(eq("dashboard:analytics"), anyString(), eq(300L), eq(TimeUnit.SECONDS));
    }

    @Test
    void testInvalidateCache_Success() {
        when(redisTemplate.delete("dashboard:analytics")).thenReturn(true);

        dashboardService.invalidateCache();

        verify(redisTemplate).delete("dashboard:analytics");
    }

    @Test
    void testInvalidateCache_RedisUnavailable() {
        doThrow(new RuntimeException("Redis unavailable")).when(redisTemplate).delete("dashboard:analytics");

        assertDoesNotThrow(() -> dashboardService.invalidateCache());
    }
}
