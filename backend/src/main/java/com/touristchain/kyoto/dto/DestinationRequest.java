package com.touristchain.kyoto.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

/**
 * Alta o edición de un destino. {@code images}, {@code tags} y {@code coordinates} son opcionales:
 * si no llegan se conservan los valores actuales.
 */
public record DestinationRequest(
        @NotBlank(message = "El nombre es obligatorio") String name,

        String description,

        @NotBlank(message = "El país es obligatorio") String country,

        @NotBlank(message = "La ciudad es obligatoria") String city,

        @NotBlank(message = "La categoría es obligatoria") String category,

        @NotNull(message = "El precio por día es obligatorio") @PositiveOrZero(message = "El precio por día debe ser positivo o cero") Double pricePerDay,

        String currency,

        List<String> images,

        List<String> tags,

        @Valid Coordinates coordinates,

        String contractAddress,

        String providerWallet) {

    public record Coordinates(@NotNull Double lat, @NotNull Double lng) {
    }

    public DestinationRequest {
        if (currency == null || currency.isBlank()) {
            currency = "USD";
        }
    }
}
