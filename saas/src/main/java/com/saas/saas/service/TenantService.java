package com.saas.saas.service;

import com.saas.saas.dto.CreateTenantRequest;
import com.saas.saas.dto.TenantResponse;
import com.saas.saas.entity.Tenant;
import com.saas.saas.repository.TenantRepository;
import org.springframework.stereotype.Service;

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
}