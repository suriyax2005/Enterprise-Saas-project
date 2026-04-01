package com.saas.saas.repository;

import com.saas.saas.entity.BlacklistedToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Standard Spring Data JPA repository for blacklisted access tokens.
 */
public interface BlacklistedTokenRepository extends JpaRepository<BlacklistedToken, Long> {

    /**
     * Checks if a revoked token is present in the blacklist database table.
     */
    boolean existsByToken(String token);

    /**
     * Bulk deletes blacklisted tokens whose expiry timestamps are behind the cutoff time.
     * Requires @Modifying for write/DML execution and @Transactional for runtime transaction boundaries.
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM BlacklistedToken b WHERE b.expiryDate < :cutoff")
    void deleteByExpiryDateBefore(@Param("cutoff") LocalDateTime cutoff);
}
