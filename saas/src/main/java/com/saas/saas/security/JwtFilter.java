package com.saas.saas.security;

import com.saas.saas.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import com.saas.saas.entity.User;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import com.saas.saas.security.CurrentUser;

import java.util.List;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final UserRepository userRepository;

    private final JwtService jwtService;

    public JwtFilter(
            JwtService jwtService,

            UserRepository userRepository
    ){
        this.jwtService = jwtService;
        this.userRepository=userRepository;
    }

    @Override
    protected void doFilterInternal(

            HttpServletRequest request,

            HttpServletResponse response,

            FilterChain filterChain

    ) throws ServletException, IOException {

        System.out.println(
                "JWT FILTER ENTERED"
        );

        String authHeader =
                request.getHeader(
                        "Authorization"
                );

        System.out.println(
                "Header = " + authHeader
        );

        if(

                authHeader != null

                        &&

                        authHeader.startsWith(
                                "Bearer "
                        )

        ){

            String token =
                    authHeader.substring(
                            7
                    );

            System.out.println(
                    "Token = " + token
            );

            String email =
                    jwtService.extractEmail(
                            token
                    );

            Long tenantId =
                    jwtService.extractTenantId(
                            token
                    );

            CurrentUser currentUser =

                    new CurrentUser(

                            email,

                            tenantId

                    );

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

            UsernamePasswordAuthenticationToken auth =

                    new UsernamePasswordAuthenticationToken(

                            currentUser,

                            null,

                            List.of(

                                    new SimpleGrantedAuthority(

                                            "ROLE_"
                                                    + user.getRole()

                                    )

                            )

                    );

            System.out.println(

                    "Role = "
                            + user.getRole()

            );

            SecurityContextHolder
                    .getContext()
                    .setAuthentication(
                            auth
                    );

            System.out.println(
                    "Authenticated User = "
                            + email
            );
        }

        filterChain.doFilter(
                request,
                response
        );
    }
}