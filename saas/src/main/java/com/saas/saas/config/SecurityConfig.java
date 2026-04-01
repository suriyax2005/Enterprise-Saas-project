package com.saas.saas.config;

import com.saas.saas.security.JwtFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    public SecurityConfig(
            JwtFilter jwtFilter
    ){

        this.jwtFilter = jwtFilter;
    }

    @Bean
    public BCryptPasswordEncoder
    passwordEncoder(){

        return new BCryptPasswordEncoder();

    }

    @Bean
    public SecurityFilterChain
    securityFilterChain(
            HttpSecurity http
    ) throws Exception {

        return http

                .csrf(csrf -> csrf.disable())

                .authorizeHttpRequests(
                        auth -> auth

                                .requestMatchers(
                                        "/v1/api/auth/**",
                                        "/v1/api/tenant/**"
                                )
                                .permitAll()

                                .requestMatchers(
                                        "/test-email"
                                )
                                .permitAll()

                                .requestMatchers(
                                        "/v1/api/admin/**"
                                )
                                .hasRole("ADMIN")

                                .anyRequest()
                                .authenticated()
                )

                .addFilterBefore(

                        jwtFilter,

                        UsernamePasswordAuthenticationFilter.class

                )

                .build();
    }
}