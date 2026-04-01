package com.saas.saas.dto;

import com.saas.saas.validation.PasswordStrength;
import jakarta.validation.constraints.NotBlank;

/**
 * Data Transfer Object representing password change requests.
 * Applies @PasswordStrength checks to verify complex character composition requirements.
 */
public class ChangePasswordRequest {

    @NotBlank(message = "Old password required")
    private String oldPassword;

    @NotBlank(message = "New password required")
    @PasswordStrength
    private String newPassword;

    public ChangePasswordRequest() {
    }

    public ChangePasswordRequest(String oldPassword, String newPassword) {
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}