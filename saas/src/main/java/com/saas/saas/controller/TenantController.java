package com.saas.saas.controller;

import com.saas.saas.dto.CreateTenantRequest;
import com.saas.saas.dto.TenantResponse;
import com.saas.saas.service.TenantService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/api/tenant")
public class TenantController {

    private final TenantService
            tenantService;

    public TenantController(
            TenantService tenantService
    ){
        this.tenantService =
                tenantService;
    }

    @PostMapping
    public TenantResponse createTenant(

            @RequestBody
            CreateTenantRequest request

    ){

        return tenantService
                .createTenant(
                        request
                );

    }

}