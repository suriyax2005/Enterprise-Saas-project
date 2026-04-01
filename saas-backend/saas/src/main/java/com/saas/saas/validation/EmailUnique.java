package com.saas.saas.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Custom validation annotation ensuring that the user email is not already taken.
 * Restricts values to ensure strict account uniqueness across registration payloads.
 */
@Constraint(validatedBy = EmailUniqueValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface EmailUnique {

    String message() default "Email address is already in use";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
