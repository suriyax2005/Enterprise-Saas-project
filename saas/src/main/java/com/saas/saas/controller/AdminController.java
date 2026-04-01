package com.saas.saas.controller;

import com.saas.saas.dto.ForgotPasswordRequest;
import com.saas.saas.dto.UpdateRoleRequest;
import com.saas.saas.dto.UserResponse;
import com.saas.saas.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/api/admin")
public class AdminController {

    private final UserService userService;

    public AdminController(
            UserService userService
    ){
        this.userService = userService;
    }

    @GetMapping("/users")
    public List<UserResponse>
    getAllUsers(){

        return userService
                .getAllTenantUsers();

    }

    @PutMapping("/users/{id}/role")
    public String updateRole(

            @PathVariable
            Long id,

            @RequestBody
            UpdateRoleRequest request

    ){

        return userService
                .updateUserRole(
                        id,
                        request
                );

    }

    @DeleteMapping("/users/{id}")
    public String deleteUser(

            @PathVariable
            Long id

    ){

        return userService
                .deleteUser(
                        id
                );

    }



}