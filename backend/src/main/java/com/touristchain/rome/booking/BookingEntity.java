package com.touristchain.rome.booking;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "booking")
@Getter
@Setter
@NoArgsConstructor
public class BookingEntity {

    @Id
    @Column(name = "booking_id", columnDefinition = "uuid", nullable = false)
    private UUID bookingId;

    @Column(name = "tourist_id", columnDefinition = "uuid", nullable = false)
    private UUID touristId;

    @Column(name = "partner_id", columnDefinition = "uuid")
    private UUID partnerId;

    @Column(name = "package_id", columnDefinition = "uuid")
    private UUID packageId;

    @Column(name = "item_type", nullable = false, length = 20)
    private String itemType;

    @Column(name = "item_ref", nullable = false, length = 64)
    private String itemRef;

    @Column(name = "item_name", nullable = false, length = 150)
    private String itemName;

    @Column(name = "trip_id")
    private Long tripId;

    @Column(name = "unit_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "total_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalPrice;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private BookingStatus status = BookingStatus.PENDING;

    @Column(name = "payment_id", columnDefinition = "uuid")
    private UUID paymentId;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        if (bookingId == null) {
            bookingId = UUID.randomUUID();
        }
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public void markPaid(UUID paymentId, LocalDateTime paidAt) {
        this.paymentId = paymentId;
        this.paidAt = paidAt;
        this.status = BookingStatus.CONFIRMED;
    }

    /** Estados de la tabla; el frontend los ve en español (ver {@link #label()}). */
    public enum BookingStatus {
        PENDING("pendiente"),
        CONFIRMED("pagado"),
        CANCELLED("cancelado"),
        COMPLETED("completado");

        private final String label;

        BookingStatus(String label) {
            this.label = label;
        }

        public String label() {
            return label;
        }
    }
}
