package com.saas.saas.controller;

import com.saas.saas.dto.ChangePasswordRequest;
import com.saas.saas.dto.ProfileResponse;
import com.saas.saas.security.CurrentUser;
import com.saas.saas.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import com.saas.saas.dto.UserResponse;
import java.util.List;

@RestController
@RequestMapping("/v1/api/user")
public class UserController {

    private final UserService userService;

    public UserController(
            UserService userService
    ){

        this.userService =
                userService;

    }

    @GetMapping("/profile")
    public ProfileResponse profile(){

        Authentication auth =

                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        CurrentUser currentUser =

                (CurrentUser)
                        auth.getPrincipal();

        return userService.getProfile(
                currentUser.getEmail()
        );

    }

    @DeleteMapping("/profile")
    public String deleteProfile(){

        Authentication auth =

                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        CurrentUser currentUser =

                (CurrentUser)
                        auth.getPrincipal();

        return userService.deleteProfile(
                currentUser.getEmail()
        );

    }

    @PutMapping("/password")
    public String changePassword(

            @Valid
            @RequestBody
            ChangePasswordRequest request

    ){

        Authentication auth =

                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        CurrentUser currentUser =

                (CurrentUser)
                        auth.getPrincipal();

        return userService.changePassword(

                currentUser.getEmail(),

                request

        );

    }
    @GetMapping("/tenant-users")
    public List<UserResponse>
    getTenantUsers(){

        return userService
                .getTenantUsers();

    }

}