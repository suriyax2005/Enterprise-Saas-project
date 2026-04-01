package com.saas.saas.dto;

import java.time.LocalDateTime;

public class AuditLogResponse {

    private String action;

    private String description;

    private String userEmail;

    private String userRole;

    private Long tenantId;

    private String ipAddress;

    private String browser;

    private String operatingSystem;

    private String requestMethod;

    private String requestUrl;

    private LocalDateTime createdAt;

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

    public String getBrowser() {
        return browser;
    }

    public void setBrowser(
            String browser
    ) {
        this.browser = browser;
    }

    public String getOperatingSystem() {
        return operatingSystem;
    }

    public void setOperatingSystem(
            String operatingSystem
    ) {
        this.operatingSystem = operatingSystem;
    }

    public String getRequestMethod() {
        return requestMethod;
    }

    public void setRequestMethod(
            String requestMethod
    ) {
        this.requestMethod = requestMethod;
    }

    public String getRequestUrl() {
        return requestUrl;
    }

    public void setRequestUrl(
            String requestUrl
    ) {
        this.requestUrl = requestUrl;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(
            LocalDateTime createdAt
    ) {
        this.createdAt = createdAt;
    }

}