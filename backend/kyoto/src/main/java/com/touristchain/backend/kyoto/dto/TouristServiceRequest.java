package com.touristchain.backend.kyoto.dto;

import java.math.BigDecimal;
import java.util.List;

import com.touristchain.backend.kyoto.models.ServiceType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record TouristServiceRequest(
        @NotBlank(message = "El nombre es obligatorio") String name,

        @NotNull(message = "El tipo de servicio es obligatorio") ServiceType serviceType,

        String description,

        @NotNull(message = "El precio es obligatorio") @PositiveOrZero(message = "El precio debe ser positivo o cero") BigDecimal price,

        String currency,

        @NotBlank(message = "La ubicación es obligatoria") String location,

        String detailedAddress,

        Boolean available,

        @PositiveOrZero(message = "La capacidad máxima debe ser positiva o cero") Integer maxCapacity,

        @PositiveOrZero(message = "La disponibilidad actual debe ser positiva o cero") Integer currentAvailability,

        List<String> images,

        List<String> tags,

        String contractAddress,

        @PositiveOrZero(message = "Los días de duración deben ser positivos o cero") Integer durationDays,

        @PositiveOrZero(message = "Las horas de duración deben ser positivas o cero") Integer durationHours) {
    public TouristServiceRequest {
        if (currency == null || currency.isBlank()) {
            currency = "USD";
        }
        if (available == null) {
            available = true;
        }
    }
}