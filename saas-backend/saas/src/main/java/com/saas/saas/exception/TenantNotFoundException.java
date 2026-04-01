package com.saas.saas.exception;

/**
 * Custom exception representing a tenant not found situation (HTTP 404).
 * Extends RuntimeException to leverage standard unchecked transaction semantics in Spring.
 */
public class TenantNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public TenantNotFoundException(String message) {
        super(message);
    }
}
