package com.touristchain.geneva.tourists;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Entity
@Table(name = "tourist")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class TouristEntity {

    @Id
    @Column(name = "tourist_id", columnDefinition = "uuid", nullable = false)
    private UUID touristId;

    @Column(name = "user_id", columnDefinition = "uuid", nullable = false)
    private UUID userId;

    @Column(name = "preferred_language", length = 10)
    private String preferredLanguage;

    @Column(name = "loyalty_points")
    private Integer loyaltyPoints;

    @Column(name = "travel_preferences", columnDefinition = "jsonb")
    private String travelPreferences; // JSON string

    @Column(name = "emergency_contact_name", length = 100)
    private String emergencyContactName;

    @Column(name = "emergency_contact_phone", length = 20)
    private String emergencyContactPhone;

    @Column(name = "newsletter_subscription")
    private Boolean newsletterSubscription;

    @PrePersist
    public void prePersist() {
        if (touristId == null) {
            touristId = UUID.randomUUID();
        }
        if (loyaltyPoints == null) {
            loyaltyPoints = 0;
        }
        if (preferredLanguage == null) {
            preferredLanguage = "es";
        }
        if (newsletterSubscription == null) {
            newsletterSubscription = true;
        }
    }
}
