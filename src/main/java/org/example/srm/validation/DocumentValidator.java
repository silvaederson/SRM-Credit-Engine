package org.example.srm.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DocumentValidator implements ConstraintValidator<ValidDocument, String> {

    private static final int CPF_LENGTH = 11;
    private static final int CNPJ_LENGTH = 14;

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.trim().isEmpty()) {
            return true;
        }

        String doc = value.replaceAll("[^0-9]", "");

        if (doc.length() == CPF_LENGTH) {
            return isValidCPF(doc);
        } else if (doc.length() == CNPJ_LENGTH) {
            return isValidCNPJ(doc);
        }

        return false;
    }

    private boolean isValidCPF(String cpf) {
        // Verificação básica de CPF
        if (cpf.matches("(\\d)\\1{10}")) {
            return false;
        }

        int[] digits = cpf.chars().map(c -> c - '0').toArray();

        // Primeiro dígito verificador
        int sum = 0;
        for (int i = 0; i < 9; i++) {
            sum += digits[i] * (10 - i);
        }
        int firstDigit = 11 - (sum % 11);
        if (firstDigit >= 10) firstDigit = 0;
        if (firstDigit != digits[9]) return false;

        // Segundo dígito verificador
        sum = 0;
        for (int i = 0; i < 10; i++) {
            sum += digits[i] * (11 - i);
        }
        int secondDigit = 11 - (sum % 11);
        if (secondDigit >= 10) secondDigit = 0;
        return secondDigit == digits[10];
    }

    private boolean isValidCNPJ(String cnpj) {
        // Verificação básica de CNPJ
        if (cnpj.matches("(\\d)\\1{13}")) {
            return false;
        }

        int[] digits = cnpj.chars().map(c -> c - '0').toArray();

        // Primeiro dígito verificador
        int sum = 0;
        int[] weights1 = {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
        for (int i = 0; i < 12; i++) {
            sum += digits[i] * weights1[i];
        }
        int firstDigit = 11 - (sum % 11);
        if (firstDigit >= 10) firstDigit = 0;
        if (firstDigit != digits[12]) return false;

        // Segundo dígito verificador
        sum = 0;
        int[] weights2 = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
        for (int i = 0; i < 13; i++) {
            sum += digits[i] * weights2[i];
        }
        int secondDigit = 11 - (sum % 11);
        if (secondDigit >= 10) secondDigit = 0;
        return secondDigit == digits[13];
    }
}