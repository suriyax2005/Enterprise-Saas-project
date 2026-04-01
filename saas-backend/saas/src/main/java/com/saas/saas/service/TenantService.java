package com.saas.saas.service;

import com.saas.saas.dto.CreateTenantRequest;
import com.saas.saas.dto.TenantResponse;
import com.saas.saas.entity.Tenant;
import com.saas.saas.exception.TenantNotFoundException;
import com.saas.saas.repository.TenantRepository;
import org.springframework.stereotype.Service;

import org.springframework.cache.annotation.Cacheable;

@Service
public class TenantService {

    private final TenantRepository
            tenantRepository;

    public TenantService(
            TenantRepository tenantRepository
    ){
        this.tenantRepository =
                tenantRepository;
    }

    public TenantResponse createTenant(

            CreateTenantRequest request

    ){

        Tenant tenant =
                new Tenant();

        tenant.setName(
                request.getName()
        );

        tenantRepository.save(
                tenant
        );

        TenantResponse response =
                new TenantResponse();

        response.setId(
                tenant.getId()
        );

        response.setName(
                tenant.getName()
        );

        return response;
    }

    /**
     * Fetches a tenant by ID. Cached in the "tenants" key structure.
     * Subsequent hits for the same ID read directly from the in-memory cache.
     */
    @Cacheable(value = "tenants", key = "#id")
    public TenantResponse getTenantById(Long id) {
        Tenant tenant = tenantRepository.findById(id)
                .orElseThrow(() -> new TenantNotFoundException("Tenant not found"));

        TenantResponse response = new TenantResponse();
        response.setId(tenant.getId());
        response.setName(tenant.getName());
        return response;
    }

    /**
     * Lists all tenants registered in the system.
     */
    public java.util.List<TenantResponse> getAllTenants() {
        java.util.List<Tenant> tenants = tenantRepository.findAll();
        java.util.List<TenantResponse> responses = new java.util.ArrayList<>();
        for (Tenant t : tenants) {
            TenantResponse res = new TenantResponse();
            res.setId(t.getId());
            res.setName(t.getName());
            responses.add(res);
        }
        return responses;
    }
}