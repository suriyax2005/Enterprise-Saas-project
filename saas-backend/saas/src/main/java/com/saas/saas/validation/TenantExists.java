package com.saas.saas.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Custom validation annotation ensuring that the specified tenant ID exists in the database.
 * Prevents linking users to non-existent or orphan tenant profiles.
 */
@Constraint(validatedBy = TenantExistsValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface TenantExists {

    String message() default "Specified tenant does not exist";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
