package com.saas.saas.repository;

import com.saas.saas.entity.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TenantRepository
        extends JpaRepository<
        Tenant,
        Long
        > {
}