package com.fintech.exception;

import com.fintech.dto.ErrorResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.ServletWebRequest;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;
    private ServletWebRequest webRequest;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/test");
        webRequest = new ServletWebRequest(request);
    }

    @Test
    void testHandleAuthenticationException() {
        AuthenticationException ex = new AuthenticationException("Invalid credentials");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleAuthenticationException(ex, webRequest);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        ErrorResponse body = response.getBody();
        assertNotNull(body);
        assertEquals(401, body.getStatus());
        assertEquals("Unauthorized", body.getError());
    }

    @Test
    void testHandleRecordNotFoundException() {
        RecordNotFoundException ex = new RecordNotFoundException(1L);

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleRecordNotFoundException(ex, webRequest);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        ErrorResponse body = response.getBody();
        assertNotNull(body);
        assertEquals(404, body.getStatus());
        assertEquals("Not Found", body.getError());
    }

    @Test
    void testHandleConflictException() {
        ConflictException ex = new ConflictException("Username already exists");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleConflictException(ex, webRequest);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        ErrorResponse body = response.getBody();
        assertNotNull(body);
        assertEquals(409, body.getStatus());
        assertEquals("Conflict", body.getError());
    }

    @Test
    void testHandleRateLimitException() {
        RateLimitException ex = new RateLimitException("Too many attempts", LocalDateTime.now().plusMinutes(5));

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleRateLimitException(ex, webRequest);

        assertEquals(HttpStatus.TOO_MANY_REQUESTS, response.getStatusCode());
        ErrorResponse body = response.getBody();
        assertNotNull(body);
        assertEquals(429, body.getStatus());
        assertEquals("Too Many Requests", body.getError());
    }

    @Test
    void testHandleValidationException() {
        ValidationException ex = new ValidationException("Validation failed", List.of("Field is required"));

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleValidationException(ex, webRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ErrorResponse body = response.getBody();
        assertNotNull(body);
        assertEquals(400, body.getStatus());
        assertEquals("Bad Request", body.getError());
    }

    @Test
    void testHandleGlobalException() {
        Exception ex = new Exception("Unexpected error");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleGlobalException(ex, webRequest);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        ErrorResponse body = response.getBody();
        assertNotNull(body);
        assertEquals(500, body.getStatus());
        assertEquals("Internal Server Error", body.getError());
    }
}
