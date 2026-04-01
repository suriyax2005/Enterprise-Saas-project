package com.saas.saas.exception;

/**
 * Custom exception representing an authentication failure (HTTP 401).
 * Thrown when the client presents invalid or missing credentials/tokens.
 */
public class UnauthorizedException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public UnauthorizedException(String message) {
        super(message);
    }
}
