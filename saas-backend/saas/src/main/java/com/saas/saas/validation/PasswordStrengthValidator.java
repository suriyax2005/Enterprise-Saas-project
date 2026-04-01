package com.saas.saas.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

/**
 * Validator class executing strength checks for the @PasswordStrength constraint.
 * Verifies character ranges (digits, case letters, symbols) and character counts.
 */
public class PasswordStrengthValidator implements ConstraintValidator<PasswordStrength, String> {

    // Regex pattern: at least 1 digit, 1 lowercase, 1 uppercase, 1 special character, no whitespaces, min 8 chars
    private static final String PASSWORD_PATTERN =
            "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$";

    private final Pattern pattern = Pattern.compile(PASSWORD_PATTERN);

    @Override
    public void initialize(PasswordStrength constraintAnnotation) {
    }

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        if (password == null) {
            return false;
        }
        return pattern.matcher(password).matches();
    }
}
