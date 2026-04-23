package com.warota.shorturlservice.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.net.URI;
import java.net.URISyntaxException;

public class ValidUrlValidator implements ConstraintValidator<ValidUrl, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // 1. Check if blank (Replacement for @NotBlank)
        if (value == null || value.trim().isEmpty()) {
            return false;
        }

        // 2. Check URL format (Replacement for @URL)
        try {
            // Using java.net.URI for strict validation (modern way)
            URI uri = new URI(value);
            return uri.getScheme() != null
                    && (uri.getScheme().equalsIgnoreCase("http") || uri.getScheme().equalsIgnoreCase("https"));
        } catch (URISyntaxException e) {
            return false;
        }
    }
}
