package com.saas.saas.repository;

import com.saas.saas.entity.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VerificationTokenRepository
        extends JpaRepository<
        VerificationToken,
        Long
        > {

    Optional<VerificationToken>
    findByToken(
            String token
    );

}