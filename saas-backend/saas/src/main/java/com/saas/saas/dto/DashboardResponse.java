package com.saas.saas.dto;

import java.util.List;

/**
 * DTO carrying dashboard statistics for the admin panel.
 */
public class DashboardResponse {

    private long totalUsers;
    private long totalTenants;
    private long totalAuditLogs;
    private long unreadNotifications;
    private List<AuditLogResponse> recentActivity;

    public long getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(long totalUsers) {
        this.totalUsers = totalUsers;
    }

    public long getTotalTenants() {
        return totalTenants;
    }

    public void setTotalTenants(long totalTenants) {
        this.totalTenants = totalTenants;
    }

    public long getTotalAuditLogs() {
        return totalAuditLogs;
    }

    public void setTotalAuditLogs(long totalAuditLogs) {
        this.totalAuditLogs = totalAuditLogs;
    }

    public long getUnreadNotifications() {
        return unreadNotifications;
    }

    public void setUnreadNotifications(long unreadNotifications) {
        this.unreadNotifications = unreadNotifications;
    }

    public List<AuditLogResponse> getRecentActivity() {
        return recentActivity;
    }

    public void setRecentActivity(List<AuditLogResponse> recentActivity) {
        this.recentActivity = recentActivity;
    }
}
