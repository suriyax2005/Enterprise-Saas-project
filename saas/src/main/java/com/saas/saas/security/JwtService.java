package com.saas.saas.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {

    private final SecretKey key =
            Keys.secretKeyFor(
                    SignatureAlgorithm.HS256
            );

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
                                        +1000*60*60
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
                                        +1000L*60*60*24*7
                        )
                )

                .signWith(key)

                .compact();
    }
}