package com.saas.saas.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity mapping user profiles to the database.
 * Hardened to track failed login counts and lockout timestamps for account security.
 */
@Entity
@Table(name = "users", indexes = {
        @Index(name = "idx_users_tenant_id", columnList = "tenant_id")
})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "tenant_id")
    private Tenant tenant;

    private String name;

    @Column(unique = true)
    private String email;

    private String password;

    private String role;

    private boolean emailVerified;

    // Security Hardening Fields
    private Integer failedLoginAttempts = 0;

    private Boolean accountLocked = false;

    private LocalDateTime lockTime;

    public User() {
    }

    public User(String name, String email, String password, String role, Tenant tenant) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
        this.tenant = tenant;
        this.emailVerified = false;
        this.failedLoginAttempts = 0;
        this.accountLocked = false;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Tenant getTenant() {
        return tenant;
    }

    public void setTenant(Tenant tenant) {
        this.tenant = tenant;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public Integer getFailedLoginAttempts() {
        return failedLoginAttempts != null ? failedLoginAttempts : 0;
    }

    public void setFailedLoginAttempts(Integer failedLoginAttempts) {
        this.failedLoginAttempts = failedLoginAttempts;
    }

    public Boolean isAccountLocked() {
        return accountLocked != null ? accountLocked : false;
    }

    public void setAccountLocked(Boolean accountLocked) {
        this.accountLocked = accountLocked;
    }

    public LocalDateTime getLockTime() {
        return lockTime;
    }

    public void setLockTime(LocalDateTime lockTime) {
        this.lockTime = lockTime;
    }
}