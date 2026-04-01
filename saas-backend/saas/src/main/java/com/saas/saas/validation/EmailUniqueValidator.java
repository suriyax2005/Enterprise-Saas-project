package com.saas.saas.validation;

import com.saas.saas.repository.UserRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validator class implementing database checks for the @EmailUnique constraint.
 * Injects UserRepository using constructor injection to inspect duplicate email records.
 */
public class EmailUniqueValidator implements ConstraintValidator<EmailUnique, String> {

    private final UserRepository userRepository;

    // Spring Boot automatically injects this repository bean at runtime
    public EmailUniqueValidator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void initialize(EmailUnique constraintAnnotation) {
    }

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        if (email == null || email.trim().isEmpty()) {
            return true; // Let @NotBlank handle empty fields
        }

        // Return true if email doesn't exist (i.e. is unique), false otherwise
        return !userRepository.findByEmail(email.trim().toLowerCase()).isPresent();
    }
}
