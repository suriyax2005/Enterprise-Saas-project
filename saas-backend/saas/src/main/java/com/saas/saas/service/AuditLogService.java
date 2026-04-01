package com.saas.saas.service;

import com.saas.saas.builder.AuditLogBuilder;
import com.saas.saas.dto.AuditDashboardResponse;
import com.saas.saas.dto.AuditLogRequest;
import com.saas.saas.dto.AuditLogResponse;
import com.saas.saas.entity.AuditLog;
import com.saas.saas.repository.AuditLogRepository;
import com.saas.saas.repository.AuditLogSpecification;
import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service component managing audit logging activities.
 * Handles database persistence, Specification-driven searches, dashboard aggregations,
 * file exports (CSV, Excel, PDF), and automated cleanups.
 */
@Service
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public AuditLogService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    /**
     * Saves an audit log request as a DB record.
     */
    @Transactional
    public void save(AuditLogRequest request) {
        auditLogRepository.save(AuditLogBuilder.build(request));
    }

    /**
     * Alias method for saving log requests, resolving AuthService compilation errors.
     */
    @Transactional
    public void log(AuditLogRequest request) {
        save(request);
    }

    /**
     * Dynamic criteria filtering with pagination and sorting.
     */
    @Transactional(readOnly = true)
    public Page<AuditLogResponse> getLogs(
            Long tenantId,
            String action,
            String userEmail,
            String ipAddress,
            String requestMethod,
            LocalDateTime start,
            LocalDateTime end,
            String search,
            Pageable pageable
    ) {
        Specification<AuditLog> spec = AuditLogSpecification.getFilters(
                tenantId, action, userEmail, ipAddress, requestMethod, start, end, search
        );

        return auditLogRepository.findAll(spec, pageable).map(AuditLogBuilder::toResponse);
    }

    /**
     * Legacy method retained for backward compatibility.
     */
    @Transactional(readOnly = true)
    public Page<AuditLogResponse> getAllLogs(Long tenantId, Pageable pageable) {
        return auditLogRepository.findByTenantId(tenantId, pageable).map(AuditLogBuilder::toResponse);
    }

    /**
     * Legacy method retained for backward compatibility.
     */
    @Transactional(readOnly = true)
    public Page<AuditLogResponse> getLogsByUser(Long tenantId, String email, Pageable pageable) {
        return auditLogRepository.findByTenantIdAndUserEmail(tenantId, email, pageable).map(AuditLogBuilder::toResponse);
    }

    /**
     * Legacy method retained for backward compatibility.
     */
    @Transactional(readOnly = true)
    public Page<AuditLogResponse> getLogsByAction(Long tenantId, String action, Pageable pageable) {
        return auditLogRepository.findByTenantIdAndAction(tenantId, action, pageable).map(AuditLogBuilder::toResponse);
    }

    /**
     * Legacy method retained for backward compatibility.
     */
    @Transactional(readOnly = true)
    public Page<AuditLogResponse> getLogsBetweenDates(Long tenantId, LocalDateTime start, LocalDateTime end, Pageable pageable) {
        return auditLogRepository.findByTenantIdAndCreatedAtBetween(tenantId, start, end, pageable).map(AuditLogBuilder::toResponse);
    }

    /**
     * Gathers tenant dashboard statistics: totals, successes, failures, action counts, and recent logs.
     */
    @Transactional(readOnly = true)
    public AuditDashboardResponse getDashboardStats(Long tenantId) {
        long totalLogs = auditLogRepository.count((root, query, cb) -> cb.equal(root.get("tenantId"), tenantId));

        // Define keywords that represent failure scenarios in descriptions
        String[] failureKeywords = {"fail", "error", "wrong", "denied", "expired", "incorrect"};

        // Count successful logs: description contains none of the failure keywords
        long successLogs = auditLogRepository.count((root, query, cb) -> {
            List<jakarta.persistence.criteria.Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("tenantId"), tenantId));
            for (String keyword : failureKeywords) {
                predicates.add(cb.notLike(cb.lower(root.get("description")), "%" + keyword + "%"));
            }
            return cb.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        });

        // Count failed logs: description contains at least one failure keyword
        long failureLogs = auditLogRepository.count((root, query, cb) -> {
            List<jakarta.persistence.criteria.Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("tenantId"), tenantId));
            List<jakarta.persistence.criteria.Predicate> orPredicates = new ArrayList<>();
            for (String keyword : failureKeywords) {
                orPredicates.add(cb.like(cb.lower(root.get("description")), "%" + keyword + "%"));
            }
            predicates.add(cb.or(orPredicates.toArray(new jakarta.persistence.criteria.Predicate[0])));
            return cb.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        });

        // Action Breakdown statistics via JPQL group-by aggregation
        List<Object[]> actions = auditLogRepository.countActionsByTenantId(tenantId);
        Map<String, Long> actionBreakdown = new HashMap<>();
        for (Object[] row : actions) {
            String act = (String) row[0];
            Long count = (Long) row[1];
            actionBreakdown.put(act, count);
        }

        // Fetch 10 most recent logs for visual feeds
        Pageable topTen = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));
        List<AuditLogResponse> recentLogs = auditLogRepository.findByTenantId(tenantId, topTen)
                .map(AuditLogBuilder::toResponse)
                .getContent();

        return new AuditDashboardResponse(totalLogs, successLogs, failureLogs, actionBreakdown, recentLogs);
    }

    /**
     * Helper to retrieve all records matching filter specifications.
     */
    private List<AuditLog> getRawLogsList(
            Long tenantId,
            String action,
            String userEmail,
            String ipAddress,
            String requestMethod,
            LocalDateTime start,
            LocalDateTime end,
            String search
    ) {
        Specification<AuditLog> spec = AuditLogSpecification.getFilters(
                tenantId, action, userEmail, ipAddress, requestMethod, start, end, search
        );
        return auditLogRepository.findAll(spec, Sort.by(Sort.Direction.DESC, "createdAt"));
    }

    /**
     * Exports filtered audit logs as a CSV file (byte array).
     */
    @Transactional(readOnly = true)
    public byte[] exportToCsv(
            Long tenantId,
            String action,
            String userEmail,
            String ipAddress,
            String requestMethod,
            LocalDateTime start,
            LocalDateTime end,
            String search
    ) {
        List<AuditLog> logs = getRawLogsList(tenantId, action, userEmail, ipAddress, requestMethod, start, end, search);

        StringBuilder sb = new StringBuilder();
        // CSV Headers
        sb.append("ID,Action,Description,User Email,User Role,IP Address,Browser,OS,Method,URL,Created At\n");

        for (AuditLog log : logs) {
            sb.append(log.getId()).append(",")
                    .append(escapeCsvField(log.getAction())).append(",")
                    .append(escapeCsvField(log.getDescription())).append(",")
                    .append(escapeCsvField(log.getUserEmail())).append(",")
                    .append(escapeCsvField(log.getUserRole())).append(",")
                    .append(escapeCsvField(log.getIpAddress())).append(",")
                    .append(escapeCsvField(log.getBrowser())).append(",")
                    .append(escapeCsvField(log.getOperatingSystem())).append(",")
                    .append(escapeCsvField(log.getRequestMethod())).append(",")
                    .append(escapeCsvField(log.getRequestUrl())).append(",")
                    .append(log.getCreatedAt()).append("\n");
        }

        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }

    private String escapeCsvField(String field) {
        if (field == null) {
            return "";
        }
        if (field.contains(",") || field.contains("\"") || field.contains("\n")) {
            return "\"" + field.replace("\"", "\"\"") + "\"";
        }
        return field;
    }

    /**
     * Exports filtered audit logs as an Excel sheet (byte array) using Apache POI.
     */
    @Transactional(readOnly = true)
    public byte[] exportToExcel(
            Long tenantId,
            String action,
            String userEmail,
            String ipAddress,
            String requestMethod,
            LocalDateTime start,
            LocalDateTime end,
            String search
    ) throws IOException {
        List<AuditLog> logs = getRawLogsList(tenantId, action, userEmail, ipAddress, requestMethod, start, end, search);

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Audit Logs");

            // Header Font Styling
            org.apache.poi.ss.usermodel.Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());

            CellStyle headerCellStyle = workbook.createCellStyle();
            headerCellStyle.setFont(headerFont);
            headerCellStyle.setFillForegroundColor(IndexedColors.BLUE_GREY.getIndex());
            headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            // Columns headers
            String[] columns = {"ID", "Action", "Description", "User Email", "User Role", "IP Address", "Browser", "OS", "Method", "URL", "Created At"};
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerCellStyle);
            }

            int rowIdx = 1;
            for (AuditLog log : logs) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(log.getId());
                row.createCell(1).setCellValue(log.getAction());
                row.createCell(2).setCellValue(log.getDescription());
                row.createCell(3).setCellValue(log.getUserEmail());
                row.createCell(4).setCellValue(log.getUserRole());
                row.createCell(5).setCellValue(log.getIpAddress());
                row.createCell(6).setCellValue(log.getBrowser());
                row.createCell(7).setCellValue(log.getOperatingSystem());
                row.createCell(8).setCellValue(log.getRequestMethod());
                row.createCell(9).setCellValue(log.getRequestUrl());
                row.createCell(10).setCellValue(log.getCreatedAt().toString());
            }

            // Auto-size columns
            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();
        }
    }

    /**
     * Exports filtered audit logs as a PDF document (byte array) using OpenPDF.
     */
    @Transactional(readOnly = true)
    public byte[] exportToPdf(
            Long tenantId,
            String action,
            String userEmail,
            String ipAddress,
            String requestMethod,
            LocalDateTime start,
            LocalDateTime end,
            String search
    ) {
        List<AuditLog> logs = getRawLogsList(tenantId, action, userEmail, ipAddress, requestMethod, start, end, search);

        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            // Set Document Title
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
            Paragraph title = new Paragraph("Audit Trail Report", titleFont);
            title.setAlignment(Paragraph.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph("Generated at: " + LocalDateTime.now()));
            document.add(new Paragraph(" ")); // Spacer

            // Table with 6 columns: ID, Action, Email, IP, Method, Timestamp
            PdfPTable table = new PdfPTable(6);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10f);

            // Set Column Width ratios
            table.setWidths(new float[]{1.0f, 2.0f, 3.5f, 2.0f, 1.5f, 3.0f});

            // Table headers
            String[] headers = {"ID", "Action", "User Email", "IP Address", "Method", "Created At"};
            Font headFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9);
            for (String colHeader : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(colHeader, headFont));
                cell.setHorizontalAlignment(Paragraph.ALIGN_CENTER);
                cell.setPadding(5);
                table.addCell(cell);
            }

            // Table rows data
            Font dataFont = FontFactory.getFont(FontFactory.HELVETICA, 8);
            for (AuditLog log : logs) {
                table.addCell(new Phrase(String.valueOf(log.getId()), dataFont));
                table.addCell(new Phrase(log.getAction() != null ? log.getAction() : "", dataFont));
                table.addCell(new Phrase(log.getUserEmail() != null ? log.getUserEmail() : "", dataFont));
                table.addCell(new Phrase(log.getIpAddress() != null ? log.getIpAddress() : "", dataFont));
                table.addCell(new Phrase(log.getRequestMethod() != null ? log.getRequestMethod() : "", dataFont));
                table.addCell(new Phrase(log.getCreatedAt() != null ? log.getCreatedAt().toString() : "", dataFont));
            }

            document.add(table);
            document.close();
        } catch (Exception e) {
            throw new RuntimeException("Error during PDF document generation", e);
        }

        return out.toByteArray();
    }

    /**
     * Performance utility: cleans up audit logs older than the specified number of days.
     */
    @Transactional
    public void cleanupLogsOlderThan(int days) {
        LocalDateTime threshold = LocalDateTime.now().minusDays(days);
        auditLogRepository.deleteByCreatedAtBefore(threshold);
    }
}