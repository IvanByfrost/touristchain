package com.touristchain.geneva.administrators;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Entity
@Table(name = "administrator")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class AdministratorEntity {

    @Id
    @Column(name = "administrator_id", columnDefinition = "uuid", nullable = false)
    private UUID administratorId;

    @Column(name = "user_id", columnDefinition = "uuid", nullable = false)
    private UUID userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "admin_level", length = 20)
    private AdminLevel adminLevel;

    @PrePersist
    public void prePersist() {
        if (administratorId == null) {
            administratorId = UUID.randomUUID();
        }
        if (adminLevel == null) {
            adminLevel = AdminLevel.MODERATOR;
        }
    }

    public enum AdminLevel {
        SUPERADMIN,
        MODERATOR,
        SUPPORT
    }

}