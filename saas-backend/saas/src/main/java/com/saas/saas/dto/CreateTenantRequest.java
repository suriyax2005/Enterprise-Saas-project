package com.saas.saas.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object for tenant creation requests.
 * Enforces non-blank, length-constrained tenant name inputs.
 */
public class CreateTenantRequest {

    @NotBlank(message = "Tenant name required")
    @Size(max = 100, message = "Tenant name must not exceed 100 characters")
    private String name;

    public CreateTenantRequest() {
    }

    public CreateTenantRequest(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}