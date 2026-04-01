package com.saas.saas.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Custom annotation representing password strength constraint checks.
 * Meta-annotations:
 * - @Constraint: Binds this annotation interface to PasswordStrengthValidator.
 * - @Target: restrains target field applications to FIELDS and PARAMETERS.
 * - @Retention: marks runtime reflection scanning retention.
 */
@Constraint(validatedBy = PasswordStrengthValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface PasswordStrength {

    String message() default "Password must be at least 8 characters long, contain an uppercase letter, a lowercase letter, a digit, and a special character";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
