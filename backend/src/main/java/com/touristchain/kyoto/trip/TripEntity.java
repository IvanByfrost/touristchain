package com.touristchain.kyoto.trip;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "trip")
@Getter
@Setter
@NoArgsConstructor
public class TripEntity {

    public static final String AVAILABLE = "disponible";
    public static final String SOLD_OUT = "reservado";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String destination;

    /** Destino del catálogo (kyoto/destinations); {@code destination} es el texto que se muestra. */
    @Column(name = "destination_id")
    private Long destinationId;

    @Column(nullable = false, columnDefinition = "text")
    private String description;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    private Integer capacity;

    @Column(nullable = false)
    private Integer available;

    @Column(nullable = false, length = 20)
    private String status = AVAILABLE;

    @Column(length = 500)
    private String image;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(columnDefinition = "text[]", nullable = false)
    private List<String> tags = new ArrayList<>();

    private Double latitude;

    private Double longitude;

    @Column(nullable = false, precision = 2, scale = 1)
    private BigDecimal rating = BigDecimal.ZERO;

    @Column(name = "total_reviews", nullable = false)
    private Integer totalReviews = 0;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /** Ocupa cupos; al agotarse, el viaje pasa a "reservado". */
    public void reserveSeats(int quantity) {
        if (!AVAILABLE.equals(status) || available < quantity) {
            throw new IllegalStateException(available == 0 || !AVAILABLE.equals(status)
                    ? "El viaje no tiene cupos disponibles"
                    : "Solo quedan " + available + " lugares disponibles");
        }
        available -= quantity;
        if (available == 0) {
            status = SOLD_OUT;
        }
    }

    /** Devuelve cupos (p. ej. al cancelar una reserva); un viaje agotado vuelve a estar disponible. */
    public void releaseSeats(int quantity) {
        available = Math.min(capacity, available + quantity);
        if (SOLD_OUT.equals(status) && available > 0) {
            status = AVAILABLE;
        }
    }
}
