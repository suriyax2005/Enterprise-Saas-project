package com.saas.saas.security;

import com.saas.saas.dto.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Global Servlet Filter implementing token-bucket rate limiting via Bucket4j.
 * Inherits from OncePerRequestFilter to guarantee execution exactly once per request.
 * Identifies clients by their remote IP address and maintains two distinct limiters:
 * - Public API limits: 20 requests per minute.
 * - Login brute-force limits: 5 requests per minute.
 */
@Component
public class RateLimitingFilter extends OncePerRequestFilter {

    private static final Logger securityLogger = LoggerFactory.getLogger("SECURITY_LOGGER");

    private final Map<String, Bucket> generalLimitCache = new ConcurrentHashMap<>();
    private final Map<String, Bucket> loginLimitCache = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper;
    private final int generalCapacity;
    private final int loginCapacity;

    public RateLimitingFilter(
            ObjectMapper objectMapper,
            @org.springframework.beans.factory.annotation.Value("${app.rate-limiting.general-capacity}") int generalCapacity,
            @org.springframework.beans.factory.annotation.Value("${app.rate-limiting.login-capacity}") int loginCapacity
    ) {
        this.objectMapper = objectMapper;
        this.generalCapacity = generalCapacity;
        this.loginCapacity = loginCapacity;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String ip = request.getRemoteAddr();
        String uri = request.getRequestURI();

        // Separate rate limiting configurations for login endpoints
        if (uri.endsWith("/v1/api/auth/login")) {
            Bucket bucket = loginLimitCache.computeIfAbsent(ip, this::createNewLoginBucket);
            if (!bucket.tryConsume(1)) {
                securityLogger.warn("SECURITY AUDIT: Login rate limit breached by IP: {} on URI: {}", ip, uri);
                sendRateLimitErrorResponse(request, response, "Login attempt rate limit exceeded. Please wait a minute and try again.");
                return;
            }
        } else {
            // General API limit configuration
            Bucket bucket = generalLimitCache.computeIfAbsent(ip, this::createNewGeneralBucket);
            if (!bucket.tryConsume(1)) {
                securityLogger.warn("SECURITY AUDIT: General API rate limit breached by IP: {} on URI: {}", ip, uri);
                sendRateLimitErrorResponse(request, response, "Rate limit exceeded. Too many requests.");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private Bucket createNewGeneralBucket(String ipAddress) {
        // Refill tokens every 1 minute, capacity generalCapacity
        Bandwidth limit = Bandwidth.classic(generalCapacity, Refill.intervally(generalCapacity, Duration.ofMinutes(1)));
        return Bucket.builder().addLimit(limit).build();
    }

    private Bucket createNewLoginBucket(String ipAddress) {
        // Refill tokens every 1 minute, capacity loginCapacity
        Bandwidth limit = Bandwidth.classic(loginCapacity, Refill.intervally(loginCapacity, Duration.ofMinutes(1)));
        return Bucket.builder().addLimit(limit).build();
    }

    private void sendRateLimitErrorResponse(HttpServletRequest request, HttpServletResponse response, String message)
            throws IOException {
        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.TOO_MANY_REQUESTS.value(),
                HttpStatus.TOO_MANY_REQUESTS.getReasonPhrase(),
                message,
                request.getRequestURI()
        );

        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }

    /**
     * Resets IP rate limits, used to isolate integration test executions.
     */
    public void clearLimits() {
        generalLimitCache.clear();
        loginLimitCache.clear();
    }
}
