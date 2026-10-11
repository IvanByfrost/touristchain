package com.touristchain.kyoto.company;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

/** Formatos de entrada y salida de empresas, con los nombres de campo que usa el panel de administración. */
public final class CompanyDtos {

    private CompanyDtos() {
    }

    public record CompanyRequest(
            @NotBlank(message = "El RUC es obligatorio") @Size(max = 20, message = "El RUC admite máximo 20 caracteres") String ruc,
            @NotBlank(message = "El nombre es obligatorio") @Size(max = 150) String name,
            @NotBlank(message = "El contacto es obligatorio") @Size(max = 100) String contact,
            @NotBlank(message = "El email es obligatorio") @Email(message = "El email no es válido") @Size(max = 100) String email,
            @NotBlank(message = "El teléfono es obligatorio") @Size(max = 30) String phone,
            @Size(max = 255) String address,
            @Pattern(regexp = "activa|inactiva|pendiente", message = "Estado inválido") String status,
            @PositiveOrZero(message = "El número de trabajadores no puede ser negativo") Integer workers) {
    }

    public record CompanyView(
            Long id,
            String ruc,
            String name,
            String contact,
            String email,
            String phone,
            String address,
            String status,
            Integer workers,
            LocalDate createdAt) {

        static CompanyView from(CompanyEntity company) {
            return new CompanyView(
                    company.getId(),
                    company.getRuc(),
                    company.getName(),
                    company.getContact(),
                    company.getEmail(),
                    company.getPhone(),
                    company.getAddress(),
                    company.getStatus(),
                    company.getWorkers(),
                    company.getCreatedAt() == null ? null : company.getCreatedAt().toLocalDate());
        }
    }
}
