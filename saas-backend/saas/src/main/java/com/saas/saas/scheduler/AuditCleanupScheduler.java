package com.saas.saas.scheduler;

import com.saas.saas.service.AuditLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Scheduled cleanup component that purges historical audit logs older than 30 days.
 * Helps prevent database size bloating and keeps index scans performant.
 */
@Component
public class AuditCleanupScheduler {

    private static final Logger log = LoggerFactory.getLogger(AuditCleanupScheduler.class);
    private final AuditLogService auditLogService;
    private final int cleanupDays;

    public AuditCleanupScheduler(
            AuditLogService auditLogService,
            @Value("${app.audit.cleanup-days:30}") int cleanupDays
    ) {
        this.auditLogService = auditLogService;
        this.cleanupDays = cleanupDays;
    }

    /**
     * Scheduled task executing daily at 3:00 AM.
     * Cron expression layout: "second minute hour day-of-month month day-of-week"
     */
    @Scheduled(cron = "${app.scheduler.audit-cleanup.cron:0 0 3 * * *}")
    public void cleanupOldLogs() {
        // Clean logs older than configured retention days
        auditLogService.cleanupLogsOlderThan(cleanupDays);
        log.info("Audit cleanup scheduler executed: removed logs older than {} days", cleanupDays);
    }
}
