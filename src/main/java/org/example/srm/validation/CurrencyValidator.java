package org.example.srm.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Component
public class CurrencyValidator implements ConstraintValidator<ValidCurrency, String> {

    private static final Set<String> VALID_CURRENCIES = new HashSet<>(Arrays.asList(
            "BRL", "USD", "EUR", "GBP", "JPY", "CHF", "CAD", "AUD", "CNY", "ARS"
    ));

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.trim().isEmpty()) {
            return true; // Deixe outras validações lidarem com null/empty
        }
        return VALID_CURRENCIES.contains(value.toUpperCase());
    }
}