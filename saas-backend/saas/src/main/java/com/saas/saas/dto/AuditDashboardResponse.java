package com.saas.saas.dto;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Data Transfer Object containing analytics and statistics for the Audit Logging Dashboard.
 * Includes counters for success/failure logs, a map of action breakdowns, and recent activity.
 */
public class AuditDashboardResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private long totalLogs;
    private long successLogs;
    private long failureLogs;
    private Map<String, Long> actionBreakdown = new HashMap<>();
    private List<AuditLogResponse> recentLogs;

    public AuditDashboardResponse() {
    }

    public AuditDashboardResponse(long totalLogs, long successLogs, long failureLogs,
                                  Map<String, Long> actionBreakdown, List<AuditLogResponse> recentLogs) {
        this.totalLogs = totalLogs;
        this.successLogs = successLogs;
        this.failureLogs = failureLogs;
        this.actionBreakdown = actionBreakdown;
        this.recentLogs = recentLogs;
    }

    public long getTotalLogs() {
        return totalLogs;
    }

    public void setTotalLogs(long totalLogs) {
        this.totalLogs = totalLogs;
    }

    public long getSuccessLogs() {
        return successLogs;
    }

    public void setSuccessLogs(long successLogs) {
        this.successLogs = successLogs;
    }

    public long getFailureLogs() {
        return failureLogs;
    }

    public void setFailureLogs(long failureLogs) {
        this.failureLogs = failureLogs;
    }

    public Map<String, Long> getActionBreakdown() {
        return actionBreakdown;
    }

    public void setActionBreakdown(Map<String, Long> actionBreakdown) {
        this.actionBreakdown = actionBreakdown;
    }

    public List<AuditLogResponse> getRecentLogs() {
        return recentLogs;
    }

    public void setRecentLogs(List<AuditLogResponse> recentLogs) {
        this.recentLogs = recentLogs;
    }
}
