package com.saas.saas.dto;

import com.saas.saas.validation.EmailUnique;
import com.saas.saas.validation.PasswordStrength;
import com.saas.saas.validation.TenantExists;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object representing a user registration payload.
 * Applied with custom validation annotations to enforce email uniqueness, password strength,
 * and valid tenant association before reaching the controller.
 */
public class RegisterRequest {

    @NotBlank(message = "Name required")
    private String name;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email required")
    @EmailUnique
    private String email;

    @NotBlank(message = "Password required")
    @PasswordStrength
    private String password;

    @NotNull(message = "Tenant ID required")
    @TenantExists
    private Long tenantId;

    private String inviteToken;

    public RegisterRequest() {
    }

    public RegisterRequest(String name, String email, String password, Long tenantId) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.tenantId = tenantId;
    }

    public RegisterRequest(String name, String email, String password, Long tenantId, String inviteToken) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.tenantId = tenantId;
        this.inviteToken = inviteToken;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public String getInviteToken() {
        return inviteToken;
    }

    public void setInviteToken(String inviteToken) {
        this.inviteToken = inviteToken;
    }
}