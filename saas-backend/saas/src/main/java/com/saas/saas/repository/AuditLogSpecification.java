package com.saas.saas.repository;

import com.saas.saas.entity.AuditLog;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * JPA Specifications helper class for building dynamic queries against the audit_logs table.
 * It compiles runtime predicates based on user input, ensuring proper tenant isolation.
 */
public final class AuditLogSpecification {

    private AuditLogSpecification() {
    }

    /**
     * Constructs a Specification to dynamically query audit logs of a tenant based on optional filters.
     *
     * @param tenantId      Mandatory tenant ID to ensure strict data isolation.
     * @param action        Optional audit action filter.
     * @param userEmail     Optional user email filter (case-insensitive exact match).
     * @param ipAddress     Optional client IP address.
     * @param requestMethod Optional request method (GET, POST, etc.).
     * @param start         Optional starting date range.
     * @param end           Optional ending date range.
     * @param search        Optional generic search text matching action, email, or description.
     * @return The Specification of AuditLog.
     */
    public static Specification<AuditLog> getFilters(
            Long tenantId,
            String action,
            String userEmail,
            String ipAddress,
            String requestMethod,
            LocalDateTime start,
            LocalDateTime end,
            String search
    ) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 1. Mandatory Tenant Isolation Constraint
            predicates.add(cb.equal(root.get("tenantId"), tenantId));

            // 2. Exact filter on Audit Action
            if (action != null && !action.trim().isEmpty()) {
                predicates.add(cb.equal(root.get("action"), action.trim()));
            }

            // 3. Filter on User Email (Case Insensitive)
            if (userEmail != null && !userEmail.trim().isEmpty()) {
                predicates.add(cb.equal(cb.lower(root.get("userEmail")), userEmail.trim().toLowerCase()));
            }

            // 4. Exact filter on IP Address
            if (ipAddress != null && !ipAddress.trim().isEmpty()) {
                predicates.add(cb.equal(root.get("ipAddress"), ipAddress.trim()));
            }

            // 5. Exact filter on Request Method
            if (requestMethod != null && !requestMethod.trim().isEmpty()) {
                predicates.add(cb.equal(cb.upper(root.get("requestMethod")), requestMethod.trim().toUpperCase()));
            }

            // 6. Range query on Created Timestamp
            if (start != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), start));
            }
            if (end != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), end));
            }

            // 7. General search term (OR combination across multiple fields)
            if (search != null && !search.trim().isEmpty()) {
                String searchPattern = "%" + search.trim().toLowerCase() + "%";
                Predicate actionMatch = cb.like(cb.lower(root.get("action")), searchPattern);
                Predicate emailMatch = cb.like(cb.lower(root.get("userEmail")), searchPattern);
                Predicate descMatch = cb.like(cb.lower(root.get("description")), searchPattern);
                predicates.add(cb.or(actionMatch, emailMatch, descMatch));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
