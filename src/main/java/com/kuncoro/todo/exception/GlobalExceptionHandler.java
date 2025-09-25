package com.kuncoro.todo.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleValidationException(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {
        
        // For client-side validation issues, avoid logging stack traces
        log.warn("Validation error: {}", ex.getMessage());

        Map<String, String> errors = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors().forEach((FieldError fe) -> {
            String fieldName = fe.getField();
            String errorMessage = fe.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        // Include global/object errors (not tied to a specific field)
        ex.getBindingResult().getGlobalErrors().forEach(error -> {
            String key = error.getObjectName();
            String errorMessage = error.getDefaultMessage();
            errors.put(key, errorMessage);
        });

        ProblemDetail problem = new ProblemDetail(
                "https://example.com/problems/validation-error",
                "Validation Error",
                HttpStatus.BAD_REQUEST.value(),
                "Request validation failed",
                buildInstance(request),
                Instant.now(),
                Map.of("errors", errors)
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .header("Content-Type", MediaType.APPLICATION_PROBLEM_JSON_VALUE)
                .body(problem);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ProblemDetail> handleConstraintViolation(
            ConstraintViolationException ex,
            HttpServletRequest request) {
        log.warn("Constraint violation: {}", ex.getMessage());

        Map<String, String> errors = new LinkedHashMap<>();
        ex.getConstraintViolations().forEach(v -> {
            String path = v.getPropertyPath() != null ? v.getPropertyPath().toString() : "";
            String message = v.getMessage();
            errors.put(path, message);
        });

        ProblemDetail problem = new ProblemDetail(
                "https://example.com/problems/validation-error",
                "Validation Error",
                HttpStatus.BAD_REQUEST.value(),
                "Request validation failed",
                buildInstance(request),
                Instant.now(),
                Map.of("errors", errors)
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .header("Content-Type", MediaType.APPLICATION_PROBLEM_JSON_VALUE)
                .body(problem);
    }

    @ExceptionHandler(TodoNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleTodoNotFound(
            TodoNotFoundException ex,
            HttpServletRequest request) {
        
        // Not found is an expected condition; log without stack trace
        log.warn("Todo not found: {}", ex.getMessage());

        ProblemDetail problem = new ProblemDetail(
                "https://example.com/problems/todo-not-found",
                "Todo Not Found",
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                buildInstance(request),
                Instant.now(),
                null
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .header("Content-Type", MediaType.APPLICATION_PROBLEM_JSON_VALUE)
                .body(problem);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleGenericException(
            Exception ex,
            HttpServletRequest request) {
        
        log.error("Unexpected error", ex);

        ProblemDetail problem = new ProblemDetail(
                "https://example.com/problems/internal-server-error",
                "Internal Server Error",
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "An unexpected error occurred",
                buildInstance(request),
                Instant.now(),
                null
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .header("Content-Type", MediaType.APPLICATION_PROBLEM_JSON_VALUE)
                .body(problem);
    }

    private String buildInstance(HttpServletRequest request) {
        String uri = request.getRequestURI();
        String qs = request.getQueryString();
        return qs == null ? uri : uri + '?' + qs;
    }
}