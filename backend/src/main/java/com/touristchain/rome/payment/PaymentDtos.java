package com.touristchain.rome.payment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/** Formatos de entrada y salida de pagos, con los nombres de campo que usa el frontend. */
public final class PaymentDtos {

    private PaymentDtos() {
    }

    /** El importe no lo manda el cliente: es la suma de sus reservas pendientes. */
    public record PaymentRequest(
            @NotBlank(message = "El método de pago es obligatorio")
            @Pattern(regexp = "tarjeta|nequi|pse", message = "Método de pago no soportado") String method,
            @Size(max = 255) String description) {
    }

    public record QrPaymentRequest(@Size(max = 255) String description) {
    }

    /** {@code qrToken} solo se devuelve al crear el pago QR, a su dueño. */
    public record PaymentView(
            UUID id,
            UUID userId,
            String userName,
            double amount,
            String method,
            String status,
            LocalDate date,
            String reference,
            String description,
            String qrToken,
            LocalDateTime paidAt,
            String escrowStatus,
            LocalDateTime releasedAt,
            LocalDateTime refundedAt) {

        static PaymentView from(PaymentEntity payment, String userName, boolean withToken) {
            return new PaymentView(
                    payment.getPaymentId(),
                    payment.getUserId(),
                    userName,
                    payment.getAmount().doubleValue(),
                    payment.getMethod(),
                    payment.getStatus(),
                    payment.getCreatedAt() == null ? LocalDate.now() : payment.getCreatedAt().toLocalDate(),
                    payment.getReference(),
                    payment.getDescription(),
                    withToken ? payment.getQrToken() : null,
                    payment.getPaidAt(),
                    payment.getEscrowStatus(),
                    payment.getReleasedAt(),
                    payment.getRefundedAt());
        }
    }

    /** El frontend dibuja el QR con {@code qrPayload}; {@code confirmUrl} simula la pasarela. */
    public record QrPaymentResponse(PaymentView payment, String qrPayload, String confirmUrl) {
    }

    public record PaymentStatus(UUID id, String status, String reference, String escrowStatus) {
    }

    /** Cantidad e importe total por estado. */
    public record Summary(long count, double amount) {
    }

    /** {@code byStatus}: pendiente/completado/rechazado. {@code byEscrow}: retenido/liberado/reembolsado. */
    public record PaymentStats(long total, Map<String, Summary> byStatus, Map<String, Summary> byEscrow) {
    }
}
