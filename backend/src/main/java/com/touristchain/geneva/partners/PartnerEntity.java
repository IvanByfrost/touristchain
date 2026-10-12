package com.touristchain.geneva.partners;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "partner")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class PartnerEntity {

    @Id
    @Column(name = "partner_id", columnDefinition = "uuid", nullable = false)
    private UUID partnerId;

    @Column(name = "user_id", columnDefinition = "uuid", nullable = false)
    private UUID userId;

    @Column(name = "company_name", nullable = false, length = 100)
    private String companyName;

    @Column(name = "tax_id", length = 20, unique = true)
    private String taxId;

    @Column(name = "business_type", length = 50)
    private String businessType;

    @Column(name = "company_description", columnDefinition = "text")
    private String companyDescription;

    @Column(name = "website", length = 200)
    private String website;

    @Column(name = "contact_email", length = 100)
    private String contactEmail;

    @Column(name = "contact_phone", length = 20)
    private String contactPhone;

    @Column(name = "country_id", columnDefinition = "uuid")
    private UUID countryId;

    @Column(name = "contact_name", length = 100)
    private String contactName;

    @Column(name = "address", length = 150)
    private String address;

    @Column(name = "city", length = 100)
    private String city;

    @Column(name = "logo_url", length = 500)
    private String logoUrl;

    @Column(name = "benefits", columnDefinition = "text")
    private String benefits;

    @Builder.Default
    @Column(name = "verified", nullable = false)
    private Boolean verified = false;

    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        if (partnerId == null) {
            partnerId = UUID.randomUUID();
        }
    }
}