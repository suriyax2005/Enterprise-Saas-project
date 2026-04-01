package com.saas.saas.exception;

/**
 * Custom exception representing a resource not found situation (HTTP 404).
 * This class inherits from RuntimeException, meaning it is an unchecked exception.
 * Under Spring Data JPA, unchecked exceptions trigger transactional rollbacks.
 */
public class ResourceNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
