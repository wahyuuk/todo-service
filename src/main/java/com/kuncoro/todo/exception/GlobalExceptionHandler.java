package com.kuncoro.todo.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleValidationException(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {
        
        log.error("Validation error: ", ex);
        
        Map<String, Object> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        ProblemDetail problem = new ProblemDetail(
                "https://example.com/problems/validation-error",
                "Validation Error",
                HttpStatus.BAD_REQUEST.value(),
                "Request validation failed",
                request.getRequestURI(),
                Instant.now(),
                Map.of("errors", errors)
        );

        return ResponseEntity.badRequest()
                .header("Content-Type", "application/problem+json")
                .body(problem);
    }

    @ExceptionHandler(TodoNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleTodoNotFound(
            TodoNotFoundException ex,
            HttpServletRequest request) {
        
        log.error("Todo not found: ", ex);
        
        ProblemDetail problem = new ProblemDetail(
                "https://example.com/problems/todo-not-found",
                "Todo Not Found",
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                request.getRequestURI(),
                Instant.now(),
                null
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .header("Content-Type", "application/problem+json")
                .body(problem);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleGenericException(
            Exception ex,
            HttpServletRequest request) {
        
        log.error("Unexpected error: ", ex);
        
        ProblemDetail problem = new ProblemDetail(
                "https://example.com/problems/internal-server-error",
                "Internal Server Error",
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "An unexpected error occurred",
                request.getRequestURI(),
                Instant.now(),
                null
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .header("Content-Type", "application/problem+json")
                .body(problem);
    }
}