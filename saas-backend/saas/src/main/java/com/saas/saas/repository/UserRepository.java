package com.saas.saas.repository;

import com.saas.saas.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface UserRepository
        extends JpaRepository<User,Long>, JpaSpecificationExecutor<User> {

    List<User> findByTenantId(
            Long tenantId
    );

    Optional<User> findByEmail(
            String email
    );

    boolean existsByEmail(
            String email
    );

}