package com.touristchain.rome.payment;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "payment")
@Getter
@Setter
@NoArgsConstructor
public class PaymentEntity {

    public static final String PENDING = "pendiente";
    public static final String COMPLETED = "completado";
    public static final String REJECTED = "rechazado";

    /** Custodia del dinero de un pago completado (antes el escrow del módulo NewYork). */
    public static final String ESCROW_HELD = "retenido";
    public static final String ESCROW_RELEASED = "liberado";
    public static final String ESCROW_REFUNDED = "reembolsado";

    @Id
    @Column(name = "payment_id", columnDefinition = "uuid", nullable = false)
    private UUID paymentId = UUID.randomUUID();

    @Column(name = "user_id", columnDefinition = "uuid", nullable = false)
    private UUID userId;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, length = 20)
    private String method;

    @Column(nullable = false, length = 20)
    private String status = PENDING;

    @Column(nullable = false, unique = true, length = 30)
    private String reference;

    @Column(length = 255)
    private String description;

    @Column(name = "qr_token", unique = true, length = 64)
    private String qrToken;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    @Column(name = "escrow_status", length = 20)
    private String escrowStatus;

    @Column(name = "released_at")
    private LocalDateTime releasedAt;

    @Column(name = "refunded_at")
    private LocalDateTime refundedAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public void complete(LocalDateTime when) {
        status = COMPLETED;
        paidAt = when;
        escrowStatus = ESCROW_HELD;
    }

    /** Solo se libera o reembolsa el dinero que sigue retenido. */
    public void release(LocalDateTime when) {
        requireHeld();
        escrowStatus = ESCROW_RELEASED;
        releasedAt = when;
    }

    public void refund(LocalDateTime when) {
        requireHeld();
        escrowStatus = ESCROW_REFUNDED;
        refundedAt = when;
    }

    private void requireHeld() {
        if (!COMPLETED.equals(status) || !ESCROW_HELD.equals(escrowStatus)) {
            throw new IllegalStateException("Solo se pueden liberar o reembolsar pagos completados con el dinero retenido");
        }
    }
}
