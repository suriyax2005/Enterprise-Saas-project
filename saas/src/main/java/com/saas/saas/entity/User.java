package com.saas.saas.entity;
import jakarta.persistence.*;
import org.springframework.boot.autoconfigure.web.WebProperties;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;


@Entity
@Table(name = "users")

public class User{
    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    private Long id;

    @ManyToOne
    @JoinColumn(
            name = "tenant_id"
    )
    private Tenant tenant;

    private String name;

    @Column(unique = true)
    private String email;

    private String password;

    private String role;

    private boolean emailVerified;

    public User(){

    }

    public long getId(){
        return id;
    }

    public String getName(){
        return name;
    }
    public void setName(String name)
    {
        this.name=name;
    }

    public String getEmail(){
        return email;
    }
    public void setEmail(String email)
    {
        this.email=email;
    }

    public String getPassword(){
        return password;
    }
    public void setPassword(String password)
    {
        this.password=password;
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

    public void setTenant(
            Tenant tenant
    ) {
        this.tenant = tenant;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(
            boolean emailVerified
    ) {
        this.emailVerified = emailVerified;
    }
}