package com.saas.saas.security;

public class CurrentUser {

    private String email;

    private Long tenantId;

    public CurrentUser(
            String email,
            Long tenantId
    ){
        this.email = email;
        this.tenantId = tenantId;
    }

    public String getEmail() {
        return email;
    }

    public Long getTenantId() {
        return tenantId;
    }
}