package com.saas.saas.service;

import com.saas.saas.builder.AuditLogBuilder;
import com.saas.saas.dto.AuditLogRequest;
import com.saas.saas.dto.AuditLogResponse;
import com.saas.saas.entity.AuditLog;
import com.saas.saas.repository.AuditLogRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class AuditLogService {

    private final AuditLogRepository
            auditLogRepository;

    public AuditLogService(

            AuditLogRepository auditLogRepository

    ) {

        this.auditLogRepository =
                auditLogRepository;

    }

    /*
     * Save Audit Log
     */
    public void save(

            AuditLogRequest request

    ) {

        auditLogRepository.save(

                AuditLogBuilder.build(
                        request
                )

        );

    }

    /*
     * Get all logs of a tenant
     */
    public Page<AuditLogResponse> getAllLogs(

            Long tenantId,

            Pageable pageable

    ) {

        return auditLogRepository

                .findByTenantId(

                        tenantId,

                        pageable

                )

                .map(

                        AuditLogBuilder::toResponse

                );

    }

    /*
     * Get logs of one user
     */
    public Page<AuditLogResponse> getLogsByUser(

            Long tenantId,

            String email,

            Pageable pageable

    ) {

        return auditLogRepository

                .findByTenantIdAndUserEmail(

                        tenantId,

                        email,

                        pageable

                )

                .map(

                        AuditLogBuilder::toResponse

                );

    }

    /*
     * Get logs by action
     */
    public Page<AuditLogResponse> getLogsByAction(

            Long tenantId,

            String action,

            Pageable pageable

    ) {

        return auditLogRepository

                .findByTenantIdAndAction(

                        tenantId,

                        action,

                        pageable

                )

                .map(

                        AuditLogBuilder::toResponse

                );

    }

    /*
     * Get logs between dates
     */
    public Page<AuditLogResponse> getLogsBetweenDates(

            Long tenantId,

            java.time.LocalDateTime start,

            java.time.LocalDateTime end,

            Pageable pageable

    ) {

        return auditLogRepository

                .findByTenantIdAndCreatedAtBetween(

                        tenantId,

                        start,

                        end,

                        pageable

                )

                .map(

                        AuditLogBuilder::toResponse

                );

    }

}