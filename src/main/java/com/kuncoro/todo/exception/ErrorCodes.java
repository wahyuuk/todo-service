package com.kuncoro.todo.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Centralized error codes with default messages.
 */
@Getter
public enum ErrorCodes {
    VALIDATION_ERROR(9400, "Validation Error", HttpStatus.BAD_REQUEST, ProblemTypes.VALIDATION_ERROR),
    TODO_NOT_FOUND(9404, "Todo Not Found", HttpStatus.NOT_FOUND, ProblemTypes.NOT_FOUND);

    private final int code;
    private final String message;
    private final HttpStatus httpStatus;
    private final String problemType;

    ErrorCodes(int code, String message, HttpStatus httpStatus, String problemType) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
        this.problemType = problemType;
    }

}
