package com.fintech.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class HealthCheckControllerTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    private HealthCheckController healthCheckController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        healthCheckController = new HealthCheckController(jdbcTemplate);
    }

    @Test
    void testHealth_DatabaseHealthy() {
        when(jdbcTemplate.queryForObject("SELECT 1", Integer.class)).thenReturn(1);

        ResponseEntity<Map<String, Object>> response = healthCheckController.health();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals("UP", body.get("status"));
    }

    @Test
    void testHealth_DatabaseDown() {
        when(jdbcTemplate.queryForObject("SELECT 1", Integer.class)).thenThrow(new RuntimeException("Database error"));

        ResponseEntity<Map<String, Object>> response = healthCheckController.health();

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals("DOWN", body.get("status"));
    }
}
