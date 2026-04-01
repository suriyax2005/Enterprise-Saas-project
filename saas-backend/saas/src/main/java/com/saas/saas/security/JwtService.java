package com.saas.saas.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {

    private final SecretKey key;
    private final long jwtExpirationMs;
    private final long jwtRefreshExpirationMs;

    public JwtService(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration-ms}") long jwtExpirationMs,
            @Value("${jwt.refresh-expiration-ms}") long jwtRefreshExpirationMs
    ) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.jwtExpirationMs = jwtExpirationMs;
        this.jwtRefreshExpirationMs = jwtRefreshExpirationMs;
    }

    public String generateToken(
            String email,
            Long tenantId
    ){

        return Jwts.builder()

                .setSubject(email)

                .claim(
                        "tenantId",
                        tenantId
                )

                .setIssuedAt(
                        new Date()
                )

                .setExpiration(
                        new Date(
                                System.currentTimeMillis()
                                        + jwtExpirationMs
                        )
                )

                .signWith(key)

                .compact();
    }

    public String generateToken(
            String email,
            Long tenantId,
            String role
    ){

        return Jwts.builder()

                .setSubject(email)

                .claim(
                        "tenantId",
                        tenantId
                )

                .claim(
                        "role",
                        role
                )

                .setIssuedAt(
                        new Date()
                )

                .setExpiration(
                        new Date(
                                System.currentTimeMillis()
                                        + jwtExpirationMs
                        )
                )

                .signWith(key)

                .compact();
    }

    public String extractEmail(
            String token
    ){

        return Jwts.parser()

                .setSigningKey(key)

                .parseClaimsJws(token)

                .getBody()

                .getSubject();
    }

    public Long extractTenantId(
            String token
    ){

        Object tenantId =

                Jwts.parser()

                        .setSigningKey(key)

                        .parseClaimsJws(token)

                        .getBody()

                        .get(
                                "tenantId"
                        );

        return Long.valueOf(
                tenantId.toString()
        );

    }

    public String extractRole(
            String token
    ){
        return Jwts.parser()
                .setSigningKey(key)
                .parseClaimsJws(token)
                .getBody()
                .get("role", String.class);
    }

    public String generateRefreshToken(
            String email
    ){

        return Jwts.builder()

                .setSubject(email)

                .setIssuedAt(
                        new Date()
                )

                .setExpiration(
                        new Date(
                                System.currentTimeMillis()
                                        + jwtRefreshExpirationMs
                        )
                )

                .signWith(key)

                .compact();
    }
}