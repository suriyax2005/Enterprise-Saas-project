package com.saas.saas.dto;

import com.saas.saas.validation.PasswordStrength;
import jakarta.validation.constraints.NotBlank;

/**
 * Data Transfer Object for password reset requests.
 * Uses token-based resets and enforces complex passwords on the new password.
 */
public class ResetPasswordRequest {

    @NotBlank(message = "Reset token required")
    private String token;

    @NotBlank(message = "New password required")
    @PasswordStrength
    private String newPassword;

    public ResetPasswordRequest() {
    }

    public ResetPasswordRequest(String token, String newPassword) {
        this.token = token;
        this.newPassword = newPassword;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}