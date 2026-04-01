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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;

import java.io.IOException;

import com.saas.saas.repository.BlacklistedTokenRepository;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private static final Logger securityLogger = LoggerFactory.getLogger("SECURITY_LOGGER");
    private static final Logger log = LoggerFactory.getLogger(JwtFilter.class);

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final BlacklistedTokenRepository blacklistedTokenRepository;

    public JwtFilter(
            JwtService jwtService,
            UserRepository userRepository,
            BlacklistedTokenRepository blacklistedTokenRepository
    ){
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.blacklistedTokenRepository = blacklistedTokenRepository;
    }

    @Override
    protected void doFilterInternal(

            HttpServletRequest request,

            HttpServletResponse response,

            FilterChain filterChain

    ) throws ServletException, IOException {

        log.debug("JWT FILTER ENTERED");

        String authHeader =
                request.getHeader(
                        "Authorization"
                );

        log.debug("Header = {}", authHeader);

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

            if (blacklistedTokenRepository.existsByToken(token)) {
                securityLogger.warn("SECURITY AUDIT: Blacklisted access token rejected: {}", token);
                log.debug("JWT Access Token is blacklisted: {}", token);
                filterChain.doFilter(request, response);
                return;
            }

            log.debug("Token = {}", token);

            String email =
                    jwtService.extractEmail(
                            token
                    );

            Long tenantId =
                    jwtService.extractTenantId(
                            token
                    );

            String role =
                    jwtService.extractRole(
                            token
                    );

            if (role == null) {
                User user = userRepository.findByEmail(email)
                        .orElseThrow(() -> new RuntimeException("User not found"));
                role = user.getRole();
            }

            CurrentUser currentUser =

                    new CurrentUser(

                            email,

                            tenantId

                    );

            UsernamePasswordAuthenticationToken auth =

                    new UsernamePasswordAuthenticationToken(

                            currentUser,

                            null,

                            List.of(

                                    new SimpleGrantedAuthority(

                                            "ROLE_"
                                                    + role

                                    )

                            )

                    );

            log.debug("Role = {}", role);

            SecurityContextHolder
                    .getContext()
                    .setAuthentication(
                            auth
                    );

            log.debug("Authenticated User = {}", email);
        }

        filterChain.doFilter(
                request,
                response
        );
    }
}