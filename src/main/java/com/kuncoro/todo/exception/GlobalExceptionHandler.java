package com.kuncoro.todo.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
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

        log.warn("Validation error: {}", ex.getMessage());

        Map<String, String> errors = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors().forEach((FieldError fe) -> {
            String fieldName = fe.getField();
            String errorMessage = fe.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        ex.getBindingResult().getGlobalErrors().forEach(error -> {
            String key = error.getObjectName();
            String errorMessage = error.getDefaultMessage();
            errors.put(key, errorMessage);
        });

        ProblemDetail problem = createValidationProblem(errors, request);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PROBLEM_JSON_VALUE)
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

        ProblemDetail problem = createValidationProblem(errors, request);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PROBLEM_JSON_VALUE)
                .body(problem);
    }

    @ExceptionHandler(GlobalException.class)
    public ResponseEntity<ProblemDetail> handleGlobalException(
            GlobalException ex,
            HttpServletRequest request) {

        ErrorCodes ec = ex.getErrorCode();
        log.warn("Application error (code {}): {}", ec.getCode(), ec.getMessage());

        HttpStatus status = ec.getHttpStatus();
        String type = ec.getProblemType();
        String title = ec.getMessage();

        ProblemDetail problem = new ProblemDetail(
                type,
                title,
                status.value(),
                ec.getMessage(),
                buildInstance(request),
                Instant.now(),
                new ProblemProperties(ec.getCode(), null)
        );

        return ResponseEntity.status(status)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PROBLEM_JSON_VALUE)
                .body(problem);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleGenericException(
            Exception ex,
            HttpServletRequest request) {

        log.error("Unexpected error", ex);

        ProblemDetail problem = new ProblemDetail(
                ProblemTypes.INTERNAL_SERVER_ERROR,
                "Internal Server Error",
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "An unexpected error occurred",
                buildInstance(request),
                Instant.now(),
                null
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PROBLEM_JSON_VALUE)
                .body(problem);
    }

    private ProblemDetail createValidationProblem(Map<String, String> errors, HttpServletRequest request) {
        return new ProblemDetail(
                ProblemTypes.VALIDATION_ERROR,
                ErrorCodes.VALIDATION_ERROR.getMessage(),
                HttpStatus.BAD_REQUEST.value(),
                ErrorCodes.VALIDATION_ERROR.getMessage(),
                buildInstance(request),
                Instant.now(),
                new ProblemProperties(ErrorCodes.VALIDATION_ERROR.getCode(), errors)
        );
    }

    private String buildInstance(HttpServletRequest request) {
        String uri = request.getRequestURI();
        String qs = request.getQueryString();
        return qs == null ? uri : uri + '?' + qs;
    }
}