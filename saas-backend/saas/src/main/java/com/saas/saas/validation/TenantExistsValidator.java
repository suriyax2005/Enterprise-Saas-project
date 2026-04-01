package com.saas.saas.validation;

import com.saas.saas.repository.TenantRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validator class implementing database checks for the @TenantExists constraint.
 * Injects TenantRepository to verify that the tenant record is present in the database.
 */
public class TenantExistsValidator implements ConstraintValidator<TenantExists, Long> {

    private final TenantRepository tenantRepository;

    public TenantExistsValidator(TenantRepository tenantRepository) {
        this.tenantRepository = tenantRepository;
    }

    @Override
    public void initialize(TenantExists constraintAnnotation) {
    }

    @Override
    public boolean isValid(Long tenantId, ConstraintValidatorContext context) {
        if (tenantId == null) {
            return true; // Let @NotNull handle null checks on DTOs
        }

        return tenantRepository.existsById(tenantId);
    }
}
