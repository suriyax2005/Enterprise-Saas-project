package com.saas.saas.builder;

import com.saas.saas.constants.AuditActions;
import com.saas.saas.dto.AuditLogRequest;
import com.saas.saas.entity.AuditLog;
import com.saas.saas.entity.User;
import com.saas.saas.util.UserAgentUtil;
import jakarta.servlet.http.HttpServletRequest;
import com.saas.saas.dto.AuditLogResponse;

public class AuditLogBuilder {

    private AuditLogBuilder() {

    }

    /*
     * Creates DTO from User + Request
     */

    private static AuditLogRequest create(

            User user,

            HttpServletRequest request,

            String action,

            String description

    ) {

        AuditLogRequest audit =
                new AuditLogRequest();

        audit.setAction(action);

        audit.setDescription(description);

        audit.setUserEmail(
                user.getEmail()
        );

        audit.setUserRole(
                user.getRole()
        );

        audit.setTenantId(
                user.getTenant().getId()
        );

        audit.setIpAddress(
                request.getRemoteAddr()
        );

        String userAgent =
                request.getHeader("User-Agent");

        audit.setBrowser(

                UserAgentUtil.getBrowser(
                        userAgent
                )

        );

        audit.setOperatingSystem(

                UserAgentUtil.getOperatingSystem(
                        userAgent
                )

        );

        audit.setRequestMethod(
                request.getMethod()
        );

        audit.setRequestUrl(
                request.getRequestURI()
        );

        return audit;

    }

    /*
     * Converts DTO → Entity
     */

    public static AuditLog build(

            AuditLogRequest request

    ) {

        AuditLog audit =
                new AuditLog();

        audit.setAction(
                request.getAction()
        );

        audit.setDescription(
                request.getDescription()
        );

        audit.setUserEmail(
                request.getUserEmail()
        );

        audit.setUserRole(
                request.getUserRole()
        );

        audit.setTenantId(
                request.getTenantId()
        );

        audit.setIpAddress(
                request.getIpAddress()
        );

        audit.setBrowser(
                request.getBrowser()
        );

        audit.setOperatingSystem(
                request.getOperatingSystem()
        );

        audit.setRequestMethod(
                request.getRequestMethod()
        );

        audit.setRequestUrl(
                request.getRequestUrl()
        );

        audit.setCreatedAt(
                java.time.LocalDateTime.now()
        );

        return audit;

    }

    public static AuditLogRequest register(

            User user,

            HttpServletRequest request

    ) {

        return create(

                user,

                request,

                AuditActions.REGISTER,

                "User registered successfully"

        );

    }

    public static AuditLogRequest login(

            User user,

            HttpServletRequest request

    ) {

        return create(

                user,

                request,

                AuditActions.LOGIN,

                "User logged in successfully"

        );

    }

    public static AuditLogRequest logout(

            User user,

            HttpServletRequest request

    ) {

        return create(

                user,

                request,

                AuditActions.LOGOUT,

                "User logged out successfully"

        );

    }

    public static AuditLogRequest refresh(

            User user,

            HttpServletRequest request

    ) {

        return create(

                user,

                request,

                AuditActions.REFRESH_TOKEN,

                "Access token refreshed"

        );

    }

    public static AuditLogRequest verifyEmail(

            User user,

            HttpServletRequest request

    ) {

        return create(

                user,

                request,

                AuditActions.VERIFY_EMAIL,

                "Email verified successfully"

        );

    }

    public static AuditLogRequest forgotPassword(

            User user,

            HttpServletRequest request

    ) {

        return create(

                user,

                request,

                AuditActions.FORGOT_PASSWORD,

                "Password reset email sent"

        );

    }

    public static AuditLogRequest resetPassword(

            User user,

            HttpServletRequest request

    ) {

        return create(

                user,

                request,

                AuditActions.RESET_PASSWORD,

                "Password reset successfully"

        );

    }

    public static AuditLogResponse toResponse(

            AuditLog auditLog

    ) {

        AuditLogResponse response =
                new AuditLogResponse();

        response.setAction(
                auditLog.getAction()
        );

        response.setDescription(
                auditLog.getDescription()
        );

        response.setUserEmail(
                auditLog.getUserEmail()
        );

        response.setUserRole(
                auditLog.getUserRole()
        );

        response.setTenantId(
                auditLog.getTenantId()
        );

        response.setIpAddress(
                auditLog.getIpAddress()
        );

        response.setBrowser(
                auditLog.getBrowser()
        );

        response.setOperatingSystem(
                auditLog.getOperatingSystem()
        );

        response.setRequestMethod(
                auditLog.getRequestMethod()
        );

        response.setRequestUrl(
                auditLog.getRequestUrl()
        );

        response.setCreatedAt(
                auditLog.getCreatedAt()
        );

        return response;

    }


}