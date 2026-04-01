package com.saas.saas.controller;

import com.saas.saas.dto.AuditLogResponse;
import com.saas.saas.dto.DashboardResponse;
import com.saas.saas.entity.AuditLog;
import com.saas.saas.entity.User;
import com.saas.saas.builder.AuditLogBuilder;
import com.saas.saas.exception.ResourceNotFoundException;
import com.saas.saas.repository.AuditLogRepository;
import com.saas.saas.repository.NotificationRepository;
import com.saas.saas.repository.TenantRepository;
import com.saas.saas.repository.UserRepository;
import com.saas.saas.security.CurrentUser;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller providing dashboard statistics for the admin panel.
 */
@RestController
@RequestMapping("/v1/api/admin/dashboard")
public class DashboardController {

    private final UserRepository userRepository;
    private final TenantRepository tenantRepository;
    private final AuditLogRepository auditLogRepository;
    private final NotificationRepository notificationRepository;

    public DashboardController(
            UserRepository userRepository,
            TenantRepository tenantRepository,
            AuditLogRepository auditLogRepository,
            NotificationRepository notificationRepository
    ) {
        this.userRepository = userRepository;
        this.tenantRepository = tenantRepository;
        this.auditLogRepository = auditLogRepository;
        this.notificationRepository = notificationRepository;
    }

    @GetMapping
    public ResponseEntity<DashboardResponse> getDashboardStats(
            @AuthenticationPrincipal CurrentUser currentUser
    ) {
        User user = userRepository.findByEmail(currentUser.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        DashboardResponse response = new DashboardResponse();
        response.setTotalUsers(userRepository.count());
        response.setTotalTenants(tenantRepository.count());
        response.setTotalAuditLogs(auditLogRepository.count());
        response.setUnreadNotifications(
                notificationRepository.countByUserIdAndIsReadFalse(user.getId())
        );

        // Fetch 10 most recent audit logs
        List<AuditLog> recentLogs = auditLogRepository.findAll(
                PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"))
        ).getContent();

        List<AuditLogResponse> recentActivity = recentLogs.stream()
                .map(AuditLogBuilder::toResponse)
                .toList();

        response.setRecentActivity(recentActivity);

        return ResponseEntity.ok(response);
    }
}
