package com.saas.saas.repository;

import com.saas.saas.entity.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDateTime;

public interface AuditLogRepository
        extends JpaRepository<AuditLog, Long>, JpaSpecificationExecutor<AuditLog> {

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

    /**
     * Aggregates actions and their counts for a tenant.
     * Uses @Query to execute JPQL group-by aggregation.
     * Uses @Param to bind tenantId parameter.
     */
    @org.springframework.data.jpa.repository.Query(
            "SELECT a.action, COUNT(a) FROM AuditLog a WHERE a.tenantId = :tenantId GROUP BY a.action"
    )
    java.util.List<Object[]> countActionsByTenantId(
            @org.springframework.data.repository.query.Param("tenantId") Long tenantId
    );

    /**
     * Deletes audit log entries created before a specific cutoff timestamp.
     * Requires @Modifying since it performs a write operation (DML).
     * Requires @Transactional to execute within a transaction boundary.
     */
    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.transaction.annotation.Transactional
    @org.springframework.data.jpa.repository.Query(
            "DELETE FROM AuditLog a WHERE a.createdAt < :cutoff"
    )
    void deleteByCreatedAtBefore(
            @org.springframework.data.repository.query.Param("cutoff") LocalDateTime cutoff
    );

}