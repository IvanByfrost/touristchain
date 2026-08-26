package com.touristchain.geneva.partners;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

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

    @PrePersist
    public void prePersist() {
        if (partnerId == null) {
            partnerId = UUID.randomUUID();
        }
    }
}