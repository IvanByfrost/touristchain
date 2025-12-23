package com.touristchain.backend.kyoto.dto;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

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

        String contractAddress,

        String providerWallet) {
    public DestinationRequest {
        if (currency == null || currency.isBlank()) {
            currency = "USD";
        }
        if (images == null) {
            images = List.of();
        }
        if (tags == null) {
            tags = List.of();
        }
    }
}