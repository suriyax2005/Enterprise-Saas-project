package com.saas.saas.service;

import com.saas.saas.dto.UpdateProfileRequest;
import com.saas.saas.dto.ProfileResponse;
import com.saas.saas.entity.User;
import com.saas.saas.repository.UserRepository;
import org.springframework.stereotype.Service;
import com.saas.saas.dto.ChangePasswordRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import com.saas.saas.dto.UserResponse;
import java.util.List;
import java.util.ArrayList;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import com.saas.saas.security.CurrentUser;
import com.saas.saas.security.CurrentUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import com.saas.saas.dto.UpdateRoleRequest;

@Service
public class UserService {

    private final UserRepository
            userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(
            UserRepository userRepository, BCryptPasswordEncoder passwordEncoder
    ){

        this.userRepository =
                userRepository;

        this.passwordEncoder = passwordEncoder;
    }

    public ProfileResponse
    updateProfile(

            String email,

            UpdateProfileRequest request

    ){

        User user =

                userRepository
                        .findByEmail(email)

                        .orElseThrow(

                                () -> new RuntimeException(
                                        "User not found"
                                )

                        );

        user.setName(request.getName());

        userRepository.save(user);

        ProfileResponse response = new ProfileResponse();

        response.setName(user.getName());

        response.setEmail(user.getEmail());

        return response;

    }

    public ProfileResponse
    getProfile(

            String email

    ){

        User user =

                userRepository
                        .findByEmail(
                                email
                        )

                        .orElseThrow(

                                () -> new RuntimeException(
                                        "User not found"
                                )

                        );

        ProfileResponse response =
                new ProfileResponse();

        response.setName(

                user.getName()

        );

        response.setEmail(

                user.getEmail()

        );

        return response;

    }

    public String
    deleteProfile(

            String email

    ){

        User user =

                userRepository
                        .findByEmail(
                                email
                        )

                        .orElseThrow(

                                () -> new RuntimeException(
                                        "User not found"
                                )

                        );

        userRepository.delete(
                user
        );

        return "Account deleted";

    }

    public String
    changePassword(

            String email,

            ChangePasswordRequest request

    ){

        User user =

                userRepository
                        .findByEmail(
                                email
                        )

                        .orElseThrow(

                                () -> new RuntimeException(
                                        "User not found"
                                )

                        );

        boolean matched =

                passwordEncoder.matches(

                        request.getOldPassword(),

                        user.getPassword()

                );

        if(!matched){

            throw new RuntimeException(
                    "Old password incorrect"
            );

        }

        user.setPassword(

                passwordEncoder.encode(

                        request.getNewPassword()

                )

        );

        userRepository.save(
                user
        );

        return
                "Password updated successfully";

    }

    public List<UserResponse>
    getAllUsers(){

        List<User> users =
                userRepository.findAll();

        List<UserResponse> responses =
                new ArrayList<>();

        for(User user : users){

            UserResponse response =
                    new UserResponse();

            response.setName(
                    user.getName()
            );

            response.setEmail(
                    user.getEmail()
            );

            response.setRole(
                    user.getRole()
            );

            responses.add(
                    response
            );

        }

        return responses;

    }

    public List<UserResponse>
    getTenantUsers(){

        Authentication authentication =

                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        CurrentUser currentUser =

                (CurrentUser)
                        authentication
                                .getPrincipal();

        Long tenantId =

                currentUser
                        .getTenantId();

        List<User> users =

                userRepository
                        .findByTenantId(
                                tenantId
                        );

        List<UserResponse> responses =
                new ArrayList<>();

        for(User user : users){

            UserResponse response =
                    new UserResponse();

            response.setId(
                    user.getId()
            );

            response.setName(
                    user.getName()
            );

            response.setEmail(
                    user.getEmail()
            );

            response.setRole(
                    user.getRole()
            );

            responses.add(
                    response
            );
        }

        return responses;
    }

    private Long getCurrentTenantId(){

        Authentication authentication =

                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        CurrentUser currentUser =

                (CurrentUser)
                        authentication
                                .getPrincipal();

        return currentUser.getTenantId();
    }

    public List<UserResponse>
    getAllTenantUsers(){

        Long tenantId =
                getCurrentTenantId();

        List<User> users =

                userRepository
                        .findByTenantId(
                                tenantId
                        );

        List<UserResponse> responses =
                new ArrayList<>();

        for(User user : users){

            UserResponse response =
                    new UserResponse();

            response.setId(
                    user.getId()
            );

            response.setName(
                    user.getName()
            );

            response.setEmail(
                    user.getEmail()
            );

            response.setRole(
                    user.getRole()
            );

            responses.add(
                    response
            );
        }

        return responses;
    }

    public String updateUserRole(

            Long userId,

            UpdateRoleRequest request

    ){

        Long tenantId =
                getCurrentTenantId();

        User user =

                userRepository
                        .findById(
                                userId
                        )

                        .orElseThrow(

                                () -> new RuntimeException(
                                        "User not found"
                                )

                        );

        if(

                !user.getTenant()
                        .getId()
                        .equals(
                                tenantId
                        )

        ){

            throw new RuntimeException(
                    "Access denied"
            );
        }

        user.setRole(
                request.getRole()
        );

        userRepository.save(
                user
        );

        return "Role updated";
    }

    public String deleteUser(
            Long userId
    ){

        Long tenantId =
                getCurrentTenantId();

        User user =

                userRepository
                        .findById(
                                userId
                        )

                        .orElseThrow(

                                () -> new RuntimeException(
                                        "User not found"
                                )

                        );

        if(

                !user.getTenant()
                        .getId()
                        .equals(
                                tenantId
                        )

        ){

            throw new RuntimeException(
                    "Access denied"
            );
        }

        userRepository.delete(
                user
        );

        return "User deleted";
    }

}