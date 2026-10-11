package com.touristchain.geneva.users;

/**
 * Nombre y apellido a partir de los campos separados o de un nombre completo
 * ("Juan Pérez Gómez" -> "Juan" / "Pérez Gómez").
 */
public record PersonName(String first, String last) {

    public static PersonName resolve(String fullName, String firstName, String lastName) {
        if (!isBlank(firstName)) {
            return new PersonName(firstName.trim(), isBlank(lastName) ? null : lastName.trim());
        }
        if (isBlank(fullName)) {
            return new PersonName(null, null);
        }
        String[] parts = fullName.trim().split("\s+", 2);
        return new PersonName(parts[0], parts.length > 1 ? parts[1] : null);
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
