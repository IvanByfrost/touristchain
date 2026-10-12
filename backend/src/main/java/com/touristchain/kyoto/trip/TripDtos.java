package com.touristchain.kyoto.trip;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/** Formatos de entrada y salida de viajes, con los nombres de campo que usa el frontend. */
public final class TripDtos {

    private TripDtos() {
    }

    public record Coordinates(@NotNull Double lat, @NotNull Double lng) {
    }

    /**
     * {@code available}, {@code tags} y {@code coordinates} son opcionales: si no llegan se
     * conservan los actuales (al crear, los cupos libres arrancan iguales a la capacidad).
     * {@code destinationId} nulo deja el viaje sin destino del catálogo.
     */
    public record TripRequest(
            @NotBlank(message = "El destino es obligatorio") @Size(max = 150) String destination,
            Long destinationId,
            @NotBlank(message = "La descripción es obligatoria") String description,
            @NotNull(message = "La fecha de inicio es obligatoria") LocalDate startDate,
            @NotNull(message = "La fecha de fin es obligatoria") LocalDate endDate,
            @NotNull(message = "El precio es obligatorio")
            @PositiveOrZero(message = "El precio no puede ser negativo") BigDecimal price,
            @NotNull(message = "La capacidad es obligatoria")
            @Positive(message = "La capacidad debe ser mayor que cero") Integer capacity,
            @PositiveOrZero(message = "Los cupos disponibles no pueden ser negativos") Integer available,
            @Pattern(regexp = "disponible|reservado|completado|cancelado", message = "Estado inválido") String status,
            @Size(max = 500) String image,
            List<@NotBlank String> tags,
            @Valid Coordinates coordinates) {
    }

    /** {@code reviews} es el número de reseñas, como lo nombra el frontend. */
    public record TripView(
            Long id,
            String destination,
            Long destinationId,
            String description,
            LocalDate startDate,
            LocalDate endDate,
            double price,
            Integer capacity,
            Integer available,
            String status,
            String image,
            double rating,
            Integer reviews,
            List<String> tags,
            Coordinates coordinates,
            LocalDate createdAt) {

        static TripView from(TripEntity trip) {
            Coordinates coordinates = trip.getLatitude() == null || trip.getLongitude() == null ? null
                    : new Coordinates(trip.getLatitude(), trip.getLongitude());
            return new TripView(
                    trip.getId(),
                    trip.getDestination(),
                    trip.getDestinationId(),
                    trip.getDescription(),
                    trip.getStartDate(),
                    trip.getEndDate(),
                    trip.getPrice().doubleValue(),
                    trip.getCapacity(),
                    trip.getAvailable(),
                    trip.getStatus(),
                    trip.getImage(),
                    trip.getRating().doubleValue(),
                    trip.getTotalReviews(),
                    List.copyOf(trip.getTags()),
                    coordinates,
                    trip.getCreatedAt() == null ? null : trip.getCreatedAt().toLocalDate());
        }
    }
}
