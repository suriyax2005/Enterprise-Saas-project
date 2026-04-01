package com.saas.saas.controller;

import com.saas.saas.dto.*;
import com.saas.saas.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/v1/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(
            AuthService authService
    ) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public String register(

            @Valid
            @RequestBody
            RegisterRequest request

    ){

        return authService.register(
                request
        );

    }

    @PostMapping("/login")
    public AuthResponse login(

            @Valid
            @RequestBody
            LoginRequest request

    )
    {
        return authService.login(
                request
        );
    }

    @PostMapping("/logout")
    public LogoutResponse logout(

            @RequestBody
            RefreshTokenRequest request

    ){

        return authService.logout(
                request
        );

    }

    @PostMapping("/refresh")
    public AuthResponse refresh(

            @RequestBody
            RefreshTokenRequest request

    ){

        return authService.refresh(
                request
        );

    }

    @GetMapping("/verify")   //verify email
    public String verifyEmail(

            @RequestParam
            String token

    ){

        return authService.verifyEmail(
                token
        );

    }
    @PostMapping("/forgot-password")
    public String forgotPassword(

            @RequestBody
            ForgotPasswordRequest request

    ){

        return authService.forgotPassword(
                request
        );

    }

    @PostMapping("/reset-password")
    public String resetPassword(

            @RequestBody
            ResetPasswordRequest request

    ){

        return authService.resetPassword(
                request
        );

    }

}