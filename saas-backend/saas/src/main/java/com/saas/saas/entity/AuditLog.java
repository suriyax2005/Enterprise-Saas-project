package com.saas.saas.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;


@Entity
@Table(name = "audit_logs", indexes = {
        @Index(name = "idx_audit_logs_tenant_id", columnList = "tenantId"),
        @Index(name = "idx_audit_logs_created_at", columnList = "createdAt"),
        @Index(name = "idx_audit_logs_action", columnList = "action"),
        @Index(name = "idx_audit_logs_user_email", columnList = "userEmail")
})
public class AuditLog {

    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    private Long id;

    private String action;

    private String description;

    private String userEmail;

    private String userRole;

    private Long tenantId;

    private String ipAddress;

    private LocalDateTime createdAt;

    private String browser;

    private String operatingSystem;

    private String requestMethod;

    private String requestUrl;

    public Long getId() {
        return id;
    }

    public void setId(
            Long id
    ) {
        this.id = id;
    }

    public String getAction() {
        return action;
    }

    public void setAction(
            String action
    ) {
        this.action = action;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(
            String description
    ) {
        this.description = description;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(
            String userEmail
    ) {
        this.userEmail = userEmail;
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(
            String userRole
    ) {
        this.userRole = userRole;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(
            Long tenantId
    ) {
        this.tenantId = tenantId;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(
            String ipAddress
    ) {
        this.ipAddress = ipAddress;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(
            LocalDateTime createdAt
    ) {
        this.createdAt = createdAt;
    }

    public void setBrowser(
            String browser
    ) {
        this.browser = browser;
    }

    public String getBrowser() {
        return browser;
    }

    public void setOperatingSystem(
            String operatingSystem
    ) {
        this.operatingSystem = operatingSystem;
    }

    public String getOperatingSystem() {
        return operatingSystem;
    }

    public void setRequestMethod(
            String requestMethod
    ) {
        this.requestMethod = requestMethod;
    }

    public String getRequestMethod() {
        return requestMethod;
    }

    public void setRequestUrl(
            String requestUrl
    ) {
        this.requestUrl= requestUrl;
    }

    public String getRequestUrl() {
        return requestUrl;
    }

}