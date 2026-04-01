package com.saas.saas.repository;

import com.saas.saas.entity.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface AuditLogRepository
        extends JpaRepository<AuditLog, Long> {

    Page<AuditLog> findByTenantId(

            Long tenantId,

            Pageable pageable

    );

    Page<AuditLog> findByTenantIdAndUserEmail(

            Long tenantId,

            String userEmail,

            Pageable pageable

    );

    Page<AuditLog> findByTenantIdAndAction(

            Long tenantId,

            String action,

            Pageable pageable

    );

    Page<AuditLog> findByTenantIdAndCreatedAtBetween(

            Long tenantId,

            LocalDateTime start,

            LocalDateTime end,

            Pageable pageable

    );

}