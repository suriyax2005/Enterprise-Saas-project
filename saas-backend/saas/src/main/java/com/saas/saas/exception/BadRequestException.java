package com.saas.saas.exception;

/**
 * Custom exception representing a generic bad request situation (HTTP 400).
 * Thrown when business rules are violated or requests carry invalid parameters.
 */
public class BadRequestException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public BadRequestException(String message) {
        super(message);
    }

    public BadRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
