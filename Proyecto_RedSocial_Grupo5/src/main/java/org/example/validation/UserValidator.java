package org.example.validation;

import java.time.LocalDate;

// Clase encargada de validar los datos de entrada
public final class UserValidator {

    // Constructor privado para evitar que la clase sea instanciada.
    private UserValidator() {}

    // Verifica que un valor String no sea nulo ni esté vacío.
    public static void requireNotBlank(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("El campo '" + fieldName + "' es obligatorio.");
        }
    }

    // Valida que la fecha de nacimiento sea válida.
    public static void validateBirthDate(LocalDate date) {
        if (date == null) { // No puede ser nula
            throw new IllegalArgumentException("La fecha de nacimiento es obligatoria.");
        }
        if (date.isAfter(LocalDate.now())) { // No puede ser una fecha futura
            throw new IllegalArgumentException("La fecha de nacimiento no puede ser futura.");
        }
    }

    // Valida que la contraseña cumpla con los requisitos mínimos.
    public static void validatePassword(String password) {
        // Valida que no esté vacía
        requireNotBlank(password, "Contraseña");
        // Valida longitud mínima
        if (password.length() < 6) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 6 caracteres.");
        }
    }
}