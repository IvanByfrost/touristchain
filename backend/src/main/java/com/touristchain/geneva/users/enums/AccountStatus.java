package com.touristchain.geneva.users.enums;

public enum AccountStatus {
    pending, active, locked, suspended, disabled, reset_required, change_required;

    /** Etiqueta que usa el frontend (activo, pendiente, bloqueado, inactivo). */
    public String label() {
        return switch (this) {
            case active -> "activo";
            case pending -> "pendiente";
            case locked -> "bloqueado";
            default -> "inactivo";
        };
    }

    public static AccountStatus fromLabel(String label) {
        if (label == null) {
            return null;
        }
        return switch (label) {
            case "activo" -> active;
            case "pendiente" -> pending;
            case "bloqueado" -> locked;
            case "inactivo" -> suspended;
            default -> throw new IllegalArgumentException("Estado no válido: " + label);
        };
    }
}
