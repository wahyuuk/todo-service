package com.kuncoro.todo.exception;

import lombok.Getter;

import java.io.Serial;

/**
 * Generic application exception carrying an error code.
 */
@Getter
public class GlobalException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = -6465737935841975936L;
    private final ErrorCodes errorCode;

    public GlobalException(ErrorCodes errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

}
