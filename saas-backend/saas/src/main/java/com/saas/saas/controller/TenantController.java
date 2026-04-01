package com.saas.saas.controller;

import com.saas.saas.dto.CreateTenantRequest;
import com.saas.saas.dto.TenantResponse;
import com.saas.saas.service.TenantService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller managing tenant lifecycle registrations.
 */
@RestController
@RequestMapping("/v1/api/tenant")
public class TenantController {

    private final TenantService tenantService;

    public TenantController(TenantService tenantService) {
        this.tenantService = tenantService;
    }

    /**
     * Registers a new tenant organization.
     */
    @PostMapping
    public ResponseEntity<TenantResponse> createTenant(
            @jakarta.validation.Valid @RequestBody CreateTenantRequest request
    ) {
        TenantResponse response = tenantService.createTenant(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Lists all tenants registered in the system.
     */
    @GetMapping
    public ResponseEntity<List<TenantResponse>> getAllTenants() {
        List<TenantResponse> responses = tenantService.getAllTenants();
        return ResponseEntity.ok(responses);
    }
}