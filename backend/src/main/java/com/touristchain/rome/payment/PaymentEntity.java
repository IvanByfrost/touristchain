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

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public void complete(LocalDateTime when) {
        status = COMPLETED;
        paidAt = when;
    }
}
