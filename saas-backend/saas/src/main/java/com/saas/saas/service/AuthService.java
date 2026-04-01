package com.saas.saas.service;

import com.saas.saas.builder.AuditLogBuilder;
import com.saas.saas.dto.*;
import com.saas.saas.entity.*;
import com.saas.saas.exception.BadRequestException;
import com.saas.saas.exception.TenantNotFoundException;
import com.saas.saas.repository.*;
import com.saas.saas.security.JwtService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import com.saas.saas.service.AuditLogService;
import com.saas.saas.dto.AuditLogRequest;
import com.saas.saas.constants.AuditActions;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.LocalDateTime;
import java.util.UUID;


@Service
public class AuthService {

    private static final Logger securityLogger = LoggerFactory.getLogger("SECURITY_LOGGER");

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenRepository
            refreshTokenRepository;
    private final TenantRepository
            tenantRepository;
    private final VerificationTokenRepository
            verificationTokenRepository;

    private final EmailService
            emailService;
    private final PasswordResetTokenRepository
            passwordResetTokenRepository;
    private final AuditLogService
            auditLogService;

    private final HttpServletRequest
            httpServletRequest;
    private final BlacklistedTokenRepository
            blacklistedTokenRepository;
    private final OrganizationInvitationRepository
            invitationRepository;


    public AuthService(
            UserRepository userRepository,
            BCryptPasswordEncoder passwordEncoder,
            JwtService jwtService,
            RefreshTokenRepository refreshTokenRepository,
            TenantRepository tenantRepository,
            VerificationTokenRepository verificationTokenRepository,
            EmailService emailService,
            PasswordResetTokenRepository passwordResetTokenRepository,
            AuditLogService auditLogService,
            HttpServletRequest httpServletRequest,
            BlacklistedTokenRepository blacklistedTokenRepository,
            OrganizationInvitationRepository invitationRepository
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.refreshTokenRepository = refreshTokenRepository;
        this.tenantRepository =
                tenantRepository;
        this.verificationTokenRepository =
                verificationTokenRepository;

        this.emailService =
                emailService;
        this.passwordResetTokenRepository =
                passwordResetTokenRepository;
        this.auditLogService =
                auditLogService;

        this.httpServletRequest =
                httpServletRequest;
        this.blacklistedTokenRepository =
                blacklistedTokenRepository;
        this.invitationRepository =
                invitationRepository;
    }

    public String register(
            RegisterRequest request
    ) {

        User user = new User();

        Tenant tenant =

                tenantRepository
                        .findById(
                                request.getTenantId()
                        )

                        .orElseThrow(
                                () -> new TenantNotFoundException(
                                        "Tenant not found"
                                )
                        );

        user.setName(
                request.getName()
        );

        user.setEmail(
                request.getEmail()
        );

        user.setPassword(
                passwordEncoder.encode(request.getPassword())
        );

        String role = "USER";
        if (request.getInviteToken() != null && !request.getInviteToken().trim().isEmpty()) {
            java.util.Optional<OrganizationInvitation> optInvitation = invitationRepository.findByInviteToken(request.getInviteToken().trim());
            if (optInvitation.isPresent()) {
                OrganizationInvitation invitation = optInvitation.get();
                if (!invitation.isAccepted() && java.time.LocalDateTime.now().isBefore(invitation.getExpiresAt())) {
                    role = invitation.getRole();
                    tenant = invitation.getOrganization().getTenant();
                    invitation.setAccepted(true);
                    invitationRepository.save(invitation);
                }
            }
        }

        user.setRole(
                role
        );

        user.setTenant(
                tenant
        );

        user.setEmailVerified(
                false
        );

        userRepository.save(
                user
        );

        auditLogService.log(

                AuditLogBuilder.register(

                        user,

                        httpServletRequest

                )

        );

        String token =

                UUID.randomUUID()
                        .toString();

        VerificationToken verificationToken =
                new VerificationToken();

        verificationToken.setToken(
                token
        );

        verificationToken.setUser(
                user
        );

        verificationToken.setExpiryDate(

                LocalDateTime.now()
                        .plusHours(24)

        );

        verificationTokenRepository.save(
                verificationToken
        );

        String verificationLink =

                "http://localhost:5173"
                        +
                        "/verify-email?token="
                        +
                        token;

        emailService.sendEmail(

                user.getEmail(),

                "Verify Your Account",

                "Click the link below to verify your account:\n\n"
                        + verificationLink

        );


        return "User Registered. Verification email sent.";
    }

    public AuthResponse login(
            LoginRequest request
    ) {

        User user =
                userRepository.findByEmail(
                                request.getEmail()
                        )
                        .orElseThrow(
                                () -> new BadRequestException(
                                        "User not found"
                                )
                        );
        // 1. Check lockout status
        if (user.isAccountLocked()) {
            if (user.getLockTime() != null && LocalDateTime.now().isAfter(user.getLockTime().plusMinutes(15))) {
                user.setAccountLocked(false);
                user.setFailedLoginAttempts(0);
                user.setLockTime(null);
                userRepository.save(user);
                securityLogger.info("SECURITY AUDIT: Account automatically unlocked for email: {}", user.getEmail());
            } else {
                securityLogger.warn("SECURITY AUDIT: Blocked login attempt on locked account for email: {}", user.getEmail());
                throw new BadRequestException("Account is locked. Please try again in 15 minutes.");
            }
        }

        if (!user.isEmailVerified()) {
            securityLogger.warn("SECURITY AUDIT: Blocked login attempt for unverified email: {}", user.getEmail());
            throw new BadRequestException("Please verify your email first");
        }

        boolean matched = passwordEncoder.matches(
                request.getPassword(),
                user.getPassword()
        );

        if (!matched) {
            int attempts = user.getFailedLoginAttempts() + 1;
            user.setFailedLoginAttempts(attempts);
            if (attempts >= 5) {
                user.setAccountLocked(true);
                user.setLockTime(LocalDateTime.now());
                userRepository.save(user);
                securityLogger.error("SECURITY AUDIT: Account locked due to brute force limits: {}", user.getEmail());
                throw new BadRequestException("Wrong password. Account has been locked for 15 minutes.");
            }
            userRepository.save(user);
            securityLogger.warn("SECURITY AUDIT: Failed login attempt for email: {}. Attempts: {}", user.getEmail(), attempts);
            throw new BadRequestException("Wrong Password. Attempts left: " + (5 - attempts));
        }

        // Reset failed login counter on success
        user.setFailedLoginAttempts(0);
        userRepository.save(user);
        securityLogger.info("SECURITY AUDIT: Successful login for email: {}", user.getEmail());

        String accessToken =

                jwtService.generateToken(

                        user.getEmail(),

                        user.getTenant()
                                .getId(),

                        user.getRole()

                );

        String refreshTokenValue =

                jwtService.generateRefreshToken(
                        user.getEmail()
                );

        RefreshToken refreshToken =
                new RefreshToken();

        refreshToken.setToken(
                refreshTokenValue
        );

        refreshToken.setUser(
                user
        );

        refreshToken.setExpiryDate(

                LocalDateTime.now()
                        .plusDays(7)

        );

        refreshToken.setRevoked(
                false
        );

        refreshTokenRepository.save(
                refreshToken
        );

        AuthResponse response =
                new AuthResponse();

        response.setAccessToken(accessToken);
        response.setRefreshToken(refreshTokenValue);
        response.setUserId(user.getId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole());
        response.setTenantId(user.getTenant().getId());

        auditLogService.log(
                AuditLogBuilder.login(user, httpServletRequest)
        );

        return response;

    }

    public LogoutResponse logout(

            RefreshTokenRequest request

    ){

        RefreshToken refreshToken =

                refreshTokenRepository
                        .findByToken(
                                request.getRefreshToken()
                        )

                        .orElseThrow(

                                () -> new BadRequestException(
                                        "Refresh token not found"
                                )

                        );

        User user =
                refreshToken.getUser();

        refreshToken.setRevoked(
                true
        );

        refreshTokenRepository.save(
                refreshToken
        );

        // Extract and blacklist access token JWT on logout
        String authHeader = httpServletRequest.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String accessToken = authHeader.substring(7);
            BlacklistedToken blacklistedToken = new BlacklistedToken(
                    accessToken,
                    LocalDateTime.now().plusHours(1)
            );
            blacklistedTokenRepository.save(blacklistedToken);
            securityLogger.info("SECURITY AUDIT: Access token blacklisted on logout for user: {}", user.getEmail());
        } else {
            securityLogger.info("SECURITY AUDIT: User logged out successfully: {}", user.getEmail());
        }

        auditLogService.log(

                AuditLogBuilder.logout(

                        user,

                        httpServletRequest

                )

        );

        LogoutResponse response =
                new LogoutResponse();

        response.setMessage(
                "Logged out successfully"
        );

        return response;

    }

    public AuthResponse refresh(
            RefreshTokenRequest request
    ) {
        RefreshToken refreshToken =

                refreshTokenRepository
                        .findByToken(
                                request.getRefreshToken()
                        )

                        .orElseThrow(
                                () -> new BadRequestException(
                                        "Refresh token not found"
                                )
                        );

        if (refreshToken.isRevoked()) {

            throw new BadRequestException(
                    "Refresh token revoked"
            );

        }

        if (LocalDateTime.now().isAfter(
                refreshToken.getExpiryDate()
        )) {

            throw new BadRequestException(
                    "Refresh token expired"
            );

        }

        refreshToken.setRevoked(
                true
        );

        refreshTokenRepository.save(
                refreshToken
        );

        String accessToken =

                jwtService.generateToken(

                        refreshToken
                                .getUser()
                                .getEmail(),

                        refreshToken
                                .getUser()
                                .getTenant()
                                .getId(),

                        refreshToken
                                .getUser()
                                .getRole()

                );

        String newRefreshTokenValue =

                jwtService.generateRefreshToken(

                        refreshToken
                                .getUser()
                                .getEmail()

                );

        RefreshToken newRefreshToken =
                new RefreshToken();

        newRefreshToken.setToken(
                newRefreshTokenValue
        );

        newRefreshToken.setUser(
                refreshToken.getUser()
        );

        newRefreshToken.setExpiryDate(

                LocalDateTime.now()
                        .plusDays(7)

        );

        newRefreshToken.setRevoked(
                false
        );

        refreshTokenRepository.save(
                newRefreshToken
        );

        auditLogService.log(

                AuditLogBuilder.refresh(

                        refreshToken.getUser(),

                        httpServletRequest

                )

        );

        User refreshedUser = refreshToken.getUser();

        AuthResponse response =
                new AuthResponse();

        response.setAccessToken(accessToken);
        response.setRefreshToken(newRefreshTokenValue);
        response.setUserId(refreshedUser.getId());
        response.setName(refreshedUser.getName());
        response.setEmail(refreshedUser.getEmail());
        response.setRole(refreshedUser.getRole());
        response.setTenantId(refreshedUser.getTenant().getId());

        return response;
    }

    public String verifyEmail(
            String token
    ) {

        VerificationToken verificationToken =

                verificationTokenRepository
                        .findByToken(
                                token
                        )
                        .orElseThrow(
                                () -> new BadRequestException(
                                        "Invalid token"
                                )
                        );

        if (LocalDateTime.now().isAfter(

                verificationToken
                        .getExpiryDate()

        )) {

            throw new BadRequestException(
                    "Token expired"
            );

        }

        User user =
                verificationToken.getUser();

        user.setEmailVerified(
                true
        );

        userRepository.save(
                user
        );

        auditLogService.log(

                AuditLogBuilder.verifyEmail(

                        user,

                        httpServletRequest

                )

        );

        return "Email verified successfully";

    }

    public String forgotPassword(

            ForgotPasswordRequest request

    ){

        User user =

                userRepository
                        .findByEmail(
                                request.getEmail()
                        )
                        .orElseThrow(
                                () -> new BadRequestException(
                                        "User not found"
                                )
                        );

        String token =
                UUID.randomUUID()
                        .toString();

        PasswordResetToken resetToken =

                passwordResetTokenRepository
                        .findByUser(user)
                        .orElseGet(
                                PasswordResetToken::new
                        );

        resetToken.setUser(
                user
        );

        resetToken.setToken(
                token
        );

        resetToken.setExpiryDate(

                LocalDateTime.now()
                        .plusHours(1)

        );

        passwordResetTokenRepository.save(
                resetToken
        );

        String resetLink =

                "http://localhost:5173/reset-password?token="
                        + token;

        emailService.sendEmail(

                user.getEmail(),

                "Reset Password",

                "Click the link below to reset your password:\n\n"
                        + resetLink

        );

        auditLogService.log(

                AuditLogBuilder.forgotPassword(

                        user,

                        httpServletRequest

                )

        );

        return "Password reset email sent";
    }

    public String resetPassword(

            ResetPasswordRequest request

    ){

        PasswordResetToken resetToken =

                passwordResetTokenRepository
                        .findByToken(
                                request.getToken()
                        )

                        .orElseThrow(
                                () -> new BadRequestException(
                                        "Invalid token"
                                )
                        );

        if(LocalDateTime.now().isAfter(

                resetToken
                        .getExpiryDate()

        )){

            throw new BadRequestException(
                    "Token expired"
            );

        }

        User user =
                resetToken.getUser();

        user.setPassword(

                passwordEncoder.encode(

                        request.getNewPassword()

                )

        );

        userRepository.save(
                user
        );

        passwordResetTokenRepository.delete(
                resetToken
        );

        auditLogService.log(

                AuditLogBuilder.resetPassword(

                        user,

                        httpServletRequest

                )

        );

        return "Password reset successfully";

    }
}