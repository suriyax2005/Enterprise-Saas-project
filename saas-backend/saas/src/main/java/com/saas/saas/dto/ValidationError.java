package com.saas.saas.dto;

import java.io.Serializable;

/**
 * Data Transfer Object representing a validation error for a specific request field.
 * Used inside ErrorResponse to provide descriptive details to the client when a request
 * payload fails Spring's standard validation constraints (such as @NotNull, @Size, @Email).
 */
public class ValidationError implements Serializable {

    private static final long serialVersionUID = 1L;

    private String field;
    private Object rejectedValue;
    private String message;

    public ValidationError() {
    }

    public ValidationError(String field, Object rejectedValue, String message) {
        this.field = field;
        this.rejectedValue = rejectedValue;
        this.message = message;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public Object getRejectedValue() {
        return rejectedValue;
    }

    public void setRejectedValue(Object rejectedValue) {
        this.rejectedValue = rejectedValue;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
