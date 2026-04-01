package com.saas.saas.exception;

/**
 * Custom exception representing authorization denial (HTTP 403).
 * Thrown when an authenticated user attempts to access a resource beyond their granted roles/permissions.
 */
public class ForbiddenException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ForbiddenException(String message) {
        super(message);
    }
}
