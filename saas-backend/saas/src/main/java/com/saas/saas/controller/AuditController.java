package com.saas.saas.controller;

import com.saas.saas.dto.AuditDashboardResponse;
import com.saas.saas.dto.AuditLogResponse;
import com.saas.saas.security.CurrentUser;
import com.saas.saas.service.AuditLogService;
import com.saas.saas.exception.UnauthorizedException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.time.LocalDateTime;

/**
 * REST controller for retrieving and exporting audit logs.
 * Restricts access based on tenant isolation, exposing logs belonging to the authenticated tenant.
 * Uses constructor injection.
 */
@RestController
@RequestMapping("/v1/api/audit")
public class AuditController {

    private final AuditLogService auditLogService;

    public AuditController(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    /**
     * Resolves the current logged in user from SecurityContextHolder.
     */
    private CurrentUser getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof CurrentUser)) {
            throw new UnauthorizedException("User is not authenticated");
        }
        return (CurrentUser) auth.getPrincipal();
    }

    /**
     * Retrieves a paginated list of audit logs for the current tenant.
     * Supports filtering by action, email, IP, request method, date range, and general text search.
     *
     * @param action        Optional filter by action.
     * @param userEmail     Optional filter by email.
     * @param ipAddress     Optional filter by IP.
     * @param requestMethod Optional filter by HTTP method.
     * @param start         Optional filter by start timestamp.
     * @param end           Optional filter by end timestamp.
     * @param search        Optional filter by general search text.
     * @param page          Page index (0-based).
     * @param size          Page size.
     * @param sortBy        Property name to sort by.
     * @param sortDir       Direction of sort ("asc" or "desc").
     * @return Paginated list of AuditLogResponse DTOs.
     */
    @GetMapping("/logs")
    public ResponseEntity<Page<AuditLogResponse>> getLogs(
            @RequestParam(required = false) String action,
            @RequestParam(required = false) String userEmail,
            @RequestParam(required = false) String ipAddress,
            @RequestParam(required = false) String requestMethod,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir
    ) {
        CurrentUser currentUser = getCurrentUser();

        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<AuditLogResponse> logs = auditLogService.getLogs(
                currentUser.getTenantId(),
                action,
                userEmail,
                ipAddress,
                requestMethod,
                start,
                end,
                search,
                pageable
        );

        return ResponseEntity.ok(logs);
    }

    /**
     * Exposes stats/aggregations for the audit dashboard of the current tenant.
     * Returns total logs, success logs, failure logs, actions, and recent activity.
     */
    @GetMapping("/dashboard")
    public ResponseEntity<AuditDashboardResponse> getDashboard() {
        CurrentUser currentUser = getCurrentUser();
        AuditDashboardResponse dashboardStats = auditLogService.getDashboardStats(currentUser.getTenantId());
        return ResponseEntity.ok(dashboardStats);
    }

    /**
     * Exports tenant audit logs as a CSV attachment.
     */
    @GetMapping("/export/csv")
    public ResponseEntity<byte[]> exportCsv(
            @RequestParam(required = false) String action,
            @RequestParam(required = false) String userEmail,
            @RequestParam(required = false) String ipAddress,
            @RequestParam(required = false) String requestMethod,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end,
            @RequestParam(required = false) String search
    ) {
        CurrentUser currentUser = getCurrentUser();

        byte[] csvData = auditLogService.exportToCsv(
                currentUser.getTenantId(),
                action,
                userEmail,
                ipAddress,
                requestMethod,
                start,
                end,
                search
        );

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"audit_logs.csv\"")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csvData);
    }

    /**
     * Exports tenant audit logs as an Excel (XLSX) attachment.
     */
    @GetMapping("/export/excel")
    public ResponseEntity<byte[]> exportExcel(
            @RequestParam(required = false) String action,
            @RequestParam(required = false) String userEmail,
            @RequestParam(required = false) String ipAddress,
            @RequestParam(required = false) String requestMethod,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end,
            @RequestParam(required = false) String search
    ) throws IOException {
        CurrentUser currentUser = getCurrentUser();

        byte[] excelData = auditLogService.exportToExcel(
                currentUser.getTenantId(),
                action,
                userEmail,
                ipAddress,
                requestMethod,
                start,
                end,
                search
        );

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"audit_logs.xlsx\"")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(excelData);
    }

    /**
     * Exports tenant audit logs as a PDF attachment.
     */
    @GetMapping("/export/pdf")
    public ResponseEntity<byte[]> exportPdf(
            @RequestParam(required = false) String action,
            @RequestParam(required = false) String userEmail,
            @RequestParam(required = false) String ipAddress,
            @RequestParam(required = false) String requestMethod,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end,
            @RequestParam(required = false) String search
    ) {
        CurrentUser currentUser = getCurrentUser();

        byte[] pdfData = auditLogService.exportToPdf(
                currentUser.getTenantId(),
                action,
                userEmail,
                ipAddress,
                requestMethod,
                start,
                end,
                search
        );

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"audit_logs.pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfData);
    }
}
