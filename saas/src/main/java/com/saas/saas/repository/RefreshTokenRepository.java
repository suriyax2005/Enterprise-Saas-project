package com.saas.saas.repository;

import com.saas.saas.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface RefreshTokenRepository
        extends JpaRepository<
        RefreshToken,
        Long
        > {

    Optional<RefreshToken>
    findByToken(
            String token
    );

    @Modifying
    @Transactional
    @Query("""
            
            DELETE FROM RefreshToken r
            
            WHERE
            
            r.revoked = true
            
            OR
            
            r.expiryDate < CURRENT_TIMESTAMP
            
            """)
    void deleteExpiredAndRevokedTokens();
}