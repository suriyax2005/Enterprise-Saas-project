package com.saas.saas.scheduler;

import com.saas.saas.repository.BlacklistedTokenRepository;
import com.saas.saas.repository.RefreshTokenRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Scheduled component that cleans up expired tokens in the database.
 * Hardened to purge expired blacklisted access tokens (JWTs) and revoked/expired refresh tokens.
 */
@Component
public class TokenCleanupScheduler {

    private static final Logger log = LoggerFactory.getLogger(TokenCleanupScheduler.class);
    private final RefreshTokenRepository refreshTokenRepository;
    private final BlacklistedTokenRepository blacklistedTokenRepository;

    public TokenCleanupScheduler(
            RefreshTokenRepository refreshTokenRepository,
            BlacklistedTokenRepository blacklistedTokenRepository
    ) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.blacklistedTokenRepository = blacklistedTokenRepository;
    }

    /**
     * Scheduled cleanup running daily at 2:00 AM.
     * Sweeps expired refresh tokens and expired blacklisted access tokens.
     */
    @Scheduled(cron = "${app.scheduler.token-cleanup.cron:0 0 2 * * *}")
    public void cleanExpiredTokens() {
        // 1. Purge expired/revoked refresh tokens
        refreshTokenRepository.deleteExpiredAndRevokedTokens();

        // 2. Purge expired blacklisted access tokens
        blacklistedTokenRepository.deleteByExpiryDateBefore(LocalDateTime.now());

        log.info("Scheduled Token Cleanup completed: purged expired refresh tokens and expired blacklisted access tokens.");
    }
}