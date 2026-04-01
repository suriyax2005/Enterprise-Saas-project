package com.saas.saas.service;

import com.saas.saas.builder.AuditLogBuilder;
import com.saas.saas.dto.*;
import com.saas.saas.entity.*;
import com.saas.saas.repository.*;
import com.saas.saas.security.JwtService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import com.saas.saas.service.AuditLogService;
import com.saas.saas.dto.AuditLogRequest;
import com.saas.saas.constants.AuditActions;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.UUID;


@Service
public class AuthService {

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
            HttpServletRequest httpServletRequest
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
                                () -> new RuntimeException(
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

        user.setRole(
                "USER"
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

                "http://localhost:8080"
                        +
                        "/v1/api/auth/verify?token="
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
                                () -> new RuntimeException(
                                        "User not found"
                                )
                        );
        if (!user.isEmailVerified()) {

            throw new RuntimeException(
                    "Please verify your email first"
            );

        }

        boolean matched =
                passwordEncoder.matches(

                        request.getPassword(),

                        user.getPassword()

                );

        if (!matched) {

            throw new RuntimeException(
                    "Wrong Password"
            );

        }

        String accessToken =

                jwtService.generateToken(

                        user.getEmail(),

                        user.getTenant()
                                .getId()

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

        response.setAccessToken(
                accessToken
        );

        response.setRefreshToken(
                refreshTokenValue
        );

        auditLogService.log(

                AuditLogBuilder.login(

                        user,

                        httpServletRequest

                )

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

                                () -> new RuntimeException(
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
                                () -> new RuntimeException(
                                        "Refresh token not found"
                                )
                        );

        if (refreshToken.isRevoked()) {

            throw new RuntimeException(
                    "Refresh token revoked"
            );

        }

        if (LocalDateTime.now().isAfter(
                refreshToken.getExpiryDate()
        )) {

            throw new RuntimeException(
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
                                .getId()

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

        AuthResponse response =
                new AuthResponse();

        response.setAccessToken(
                accessToken
        );

        response.setRefreshToken(
                newRefreshTokenValue
        );

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
                                () -> new RuntimeException(
                                        "Invalid token"
                                )
                        );

        if (LocalDateTime.now().isAfter(

                verificationToken
                        .getExpiryDate()

        )) {

            throw new RuntimeException(
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
                                () -> new RuntimeException(
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

                "http://localhost:8080/v1/api/auth/reset-password?token="
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
                                () -> new RuntimeException(
                                        "Invalid token"
                                )
                        );

        if(LocalDateTime.now().isAfter(

                resetToken
                        .getExpiryDate()

        )){

            throw new RuntimeException(
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