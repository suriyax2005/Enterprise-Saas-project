package com.saas.saas.scheduler;

import com.saas.saas.repository.RefreshTokenRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class TokenCleanupScheduler {

    private final RefreshTokenRepository
            refreshTokenRepository;

    public TokenCleanupScheduler(

            RefreshTokenRepository refreshTokenRepository

    ){

        this.refreshTokenRepository =
                refreshTokenRepository;

    }

    @Scheduled(
            cron = "0 0 2 * * *"
    )
    public void cleanExpiredTokens(){

        refreshTokenRepository
                .deleteExpiredAndRevokedTokens();

        System.out.println(
                "Expired tokens cleaned"
        );

    }

}