package com.touristchain.geneva.reputation;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "review")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class ReviewEntity {

    @Id
    @Column(name = "review_id", columnDefinition = "uuid", nullable = false)
    private UUID reviewId;

    @Column(name = "tourist_id", columnDefinition = "uuid", nullable = false)
    private UUID touristId;

    @Column(name = "partner_id", columnDefinition = "uuid")
    private UUID partnerId;

    @Column(name = "package_id", columnDefinition = "uuid")
    private UUID packageId;

    @Column(name = "booking_id", columnDefinition = "uuid")
    private UUID bookingId;

    @Column(name = "item_type", length = 20)
    private String itemType;

    @Column(name = "item_ref", length = 64)
    private String itemRef;

    @Column(name = "item_name", length = 150)
    private String itemName;

    @Column(name = "rating", nullable = false)
    private Integer rating;

    @Column(name = "comment", columnDefinition = "text")
    private String comment;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        if (reviewId == null) {
            reviewId = UUID.randomUUID();
        }
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}