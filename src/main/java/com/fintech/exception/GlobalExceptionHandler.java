package com.fintech.exception;

import com.fintech.dto.ErrorResponse;
import com.fintech.dto.FieldError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
        ValidationException ex,
        WebRequest request
    ) {
        List<FieldError> fieldErrors = new ArrayList<>();
        for (String error : ex.getErrors()) {
            fieldErrors.add(new FieldError("field", error));
        }

        ErrorResponse errorResponse = new ErrorResponse(
            LocalDateTime.now(),
            400,
            "Bad Request",
            ex.getMessage(),
            fieldErrors,
            request.getDescription(false).replace("uri=", "")
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(
        AuthenticationException ex,
        WebRequest request
    ) {
        ErrorResponse errorResponse = new ErrorResponse(
            LocalDateTime.now(),
            401,
            "Unauthorized",
            ex.getMessage(),
            new ArrayList<>(),
            request.getDescription(false).replace("uri=", "")
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(
        AccessDeniedException ex,
        WebRequest request
    ) {
        ErrorResponse errorResponse = new ErrorResponse(
            LocalDateTime.now(),
            403,
            "Forbidden",
            "Access denied",
            new ArrayList<>(),
            request.getDescription(false).replace("uri=", "")
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(RecordNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleRecordNotFoundException(
        RecordNotFoundException ex,
        WebRequest request
    ) {
        ErrorResponse errorResponse = new ErrorResponse(
            LocalDateTime.now(),
            404,
            "Not Found",
            ex.getMessage(),
            new ArrayList<>(),
            request.getDescription(false).replace("uri=", "")
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorResponse> handleConflictException(
        ConflictException ex,
        WebRequest request
    ) {
        ErrorResponse errorResponse = new ErrorResponse(
            LocalDateTime.now(),
            409,
            "Conflict",
            ex.getMessage(),
            new ArrayList<>(),
            request.getDescription(false).replace("uri=", "")
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(RateLimitException.class)
    public ResponseEntity<ErrorResponse> handleRateLimitException(
        RateLimitException ex,
        WebRequest request
    ) {
        ErrorResponse errorResponse = new ErrorResponse(
            LocalDateTime.now(),
            429,
            "Too Many Requests",
            ex.getMessage(),
            new ArrayList<>(),
            request.getDescription(false).replace("uri=", "")
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.TOO_MANY_REQUESTS);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(
        MethodArgumentNotValidException ex,
        WebRequest request
    ) {
        List<FieldError> fieldErrors = new ArrayList<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
            fieldErrors.add(new FieldError(error.getField(), error.getDefaultMessage()))
        );

        ErrorResponse errorResponse = new ErrorResponse(
            LocalDateTime.now(),
            400,
            "Bad Request",
            "Validation failed",
            fieldErrors,
            request.getDescription(false).replace("uri=", "")
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(
        Exception ex,
        WebRequest request
    ) {
        ErrorResponse errorResponse = new ErrorResponse(
            LocalDateTime.now(),
            500,
            "Internal Server Error",
            "An unexpected error occurred",
            new ArrayList<>(),
            request.getDescription(false).replace("uri=", "")
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
