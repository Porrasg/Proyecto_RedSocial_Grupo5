package org.example.validation;

import java.time.LocalDate;

public final class UserValidator {

    private UserValidator() {}

    public static void requireNotBlank(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("El campo '" + fieldName + "' es obligatorio.");
        }
    }

    public static void validateBirthDate(LocalDate date) {
        if (date == null) {
            throw new IllegalArgumentException("La fecha de nacimiento es obligatoria.");
        }
        if (date.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha de nacimiento no puede ser futura.");
        }
    }

    public static void validatePassword(String password) {
        requireNotBlank(password, "Contraseña");
        if (password.length() < 6) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 6 caracteres.");
        }
    }
}