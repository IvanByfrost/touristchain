package com.touristchain.rome.booking;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/** Formatos de entrada y salida de reservas, con los nombres de campo que usa el frontend. */
public final class BookingDtos {

    private BookingDtos() {
    }

    /**
     * El cliente solo dice qué reserva y cuánto; nombre y precio los pone el servidor.
     * {@code itemId} es el id del viaje, hotel o vehículo, o el plan del paquete (basico, medio, black).
     * {@code quantity} son personas (viaje), noches (hotel), días (renta) o 1 (paquete).
     */
    public record BookingRequest(
            @NotBlank(message = "El tipo de ítem es obligatorio")
            @Pattern(regexp = "viaje|hotel|renta|paquete", message = "Tipo de ítem inválido") String itemType,
            @NotBlank(message = "El ítem es obligatorio") String itemId,
            @NotNull(message = "La cantidad es obligatoria")
            @Positive(message = "La cantidad debe ser mayor que cero")
            @Max(value = 30, message = "La cantidad máxima por reserva es 30") Integer quantity) {
    }

    /** {@code byStatus} usa las etiquetas del frontend; {@code revenue} suma las pagadas y completadas. */
    public record BookingStats(long total, Map<String, Long> byStatus, double revenue) {
    }

    public record BookingView(
            UUID id,
            UUID userId,
            String userName,
            String itemType,
            String itemId,
            String itemName,
            Long tripId,
            double price,
            Integer quantity,
            double total,
            LocalDate startDate,
            LocalDate endDate,
            String status,
            UUID paymentId,
            LocalDateTime paidAt,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {

        static BookingView from(BookingEntity booking, UUID userId, String userName) {
            return new BookingView(
                    booking.getBookingId(),
                    userId,
                    userName,
                    booking.getItemType(),
                    booking.getItemRef(),
                    booking.getItemName(),
                    booking.getTripId(),
                    booking.getUnitPrice().doubleValue(),
                    booking.getQuantity(),
                    booking.getTotalPrice().doubleValue(),
                    booking.getStartDate(),
                    booking.getEndDate(),
                    booking.getStatus().label(),
                    booking.getPaymentId(),
                    booking.getPaidAt(),
                    booking.getCreatedAt(),
                    booking.getUpdatedAt());
        }
    }
}
