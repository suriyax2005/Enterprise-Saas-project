package com.saas.saas.repository;

import com.saas.saas.entity.PasswordResetToken;
import com.saas.saas.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordResetTokenRepository
        extends JpaRepository<
        PasswordResetToken,
        Long
        > {

    Optional<PasswordResetToken> findByToken(
            String token
    );

    Optional<PasswordResetToken> findByUser(
            User user
    );
}