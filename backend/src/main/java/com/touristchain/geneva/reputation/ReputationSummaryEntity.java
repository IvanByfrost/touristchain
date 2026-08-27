package com.touristchain.geneva.reputation;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "reputation_summary")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class ReputationSummaryEntity {

    @Id
    @Column(name = "reputation_summary_id", columnDefinition = "uuid", nullable = false)
    private UUID reputationSummaryId;

    @Enumerated(EnumType.STRING)
    @Column(name = "target_type", nullable = false, length = 20)
    private TargetType targetType;

    @Column(name = "target_id", columnDefinition = "uuid", nullable = false)
    private UUID targetId;

    @Column(name = "average_rating", precision = 3, scale = 2)
    private BigDecimal averageRating;

    @Column(name = "total_reviews")
    private Integer totalReviews;

    @Column(name = "rating_1_count")
    private Integer rating1Count;

    @Column(name = "rating_2_count")
    private Integer rating2Count;

    @Column(name = "rating_3_count")
    private Integer rating3Count;

    @Column(name = "rating_4_count")
    private Integer rating4Count;

    @Column(name = "rating_5_count")
    private Integer rating5Count;

    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;

    @PrePersist
    protected void onCreate() {
        if (reputationSummaryId == null) {
            reputationSummaryId = UUID.randomUUID();
        }
        if (averageRating == null) {
            averageRating = BigDecimal.ZERO;
        }
        if (totalReviews == null) {
            totalReviews = 0;
        }
        if (rating1Count == null)
            rating1Count = 0;
        if (rating2Count == null)
            rating2Count = 0;
        if (rating3Count == null)
            rating3Count = 0;
        if (rating4Count == null)
            rating4Count = 0;
        if (rating5Count == null)
            rating5Count = 0;
        lastUpdated = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        lastUpdated = LocalDateTime.now();
    }

    public enum TargetType {
        TOURIST,
        PARTNER
    }

}