package com.saas.saas.controller;

import com.saas.saas.dto.ChangePasswordRequest;
import com.saas.saas.dto.ProfileResponse;
import com.saas.saas.dto.UpdateProfileRequest;
import com.saas.saas.security.CurrentUser;
import com.saas.saas.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controller managing personal user profile and security credentials.
 */
@RestController
@RequestMapping("/v1/api/users/profile")
public class ProfileController {

    private final UserService userService;

    public ProfileController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Retrieves the profile details of the authenticated user.
     */
    @GetMapping
    public ResponseEntity<ProfileResponse> getProfile(@AuthenticationPrincipal CurrentUser currentUser) {
        ProfileResponse profile = userService.getProfile(currentUser.getEmail());
        return ResponseEntity.ok(profile);
    }

    /**
     * Updates the authenticated user's profile details.
     */
    @PutMapping
    public ResponseEntity<ProfileResponse> updateProfile(
            @AuthenticationPrincipal CurrentUser currentUser,
            @Valid @RequestBody UpdateProfileRequest request
    ) {
        ProfileResponse profile = userService.updateProfile(currentUser.getEmail(), request);
        return ResponseEntity.ok(profile);
    }

    /**
     * Modifies the user's login password.
     */
    @PutMapping("/change-password")
    public ResponseEntity<Map<String, String>> changePassword(
            @AuthenticationPrincipal CurrentUser currentUser,
            @Valid @RequestBody ChangePasswordRequest request
    ) {
        String message = userService.changePassword(currentUser.getEmail(), request);
        return ResponseEntity.ok(Map.of("message", message));
    }

    /**
     * Deletes the user profile from the database.
     */
    @DeleteMapping
    public ResponseEntity<Map<String, String>> deleteProfile(@AuthenticationPrincipal CurrentUser currentUser) {
        String message = userService.deleteProfile(currentUser.getEmail());
        return ResponseEntity.ok(Map.of("message", message));
    }
}
