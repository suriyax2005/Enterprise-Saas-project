package com.saas.saas.repository;

import com.saas.saas.entity.User;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;

import java.util.ArrayList;
import java.util.List;

/**
 * JPA Specifications builder for dynamic querying of the Users table.
 * Translates client search parameters into type-safe SQL predicates.
 */
public class UserSpecification {

    /**
     * Builds a composite Jpa Specification based on optional query parameters.
     */
    public static Specification<User> filterUsers(
            String name,
            String email,
            String role,
            Long tenantId
    ) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 1. Filter by Name (like, case-insensitive)
            if (name != null && !name.trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("name")),
                        "%" + name.trim().toLowerCase() + "%"
                ));
            }

            // 2. Filter by Email (like, case-insensitive)
            if (email != null && !email.trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("email")),
                        "%" + email.trim().toLowerCase() + "%"
                ));
            }

            // 3. Filter by Role (exact match)
            if (role != null && !role.trim().isEmpty()) {
                predicates.add(criteriaBuilder.equal(
                        root.get("role"),
                        role.trim().toUpperCase()
                ));
            }

            // 4. Enforce Multi-tenant isolation boundary if tenant ID is supplied
            if (tenantId != null) {
                predicates.add(criteriaBuilder.equal(
                        root.get("tenant").get("id"),
                        tenantId
                ));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
