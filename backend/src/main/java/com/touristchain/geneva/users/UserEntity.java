package com.touristchain.geneva.users;

import com.touristchain.geneva.users.enums.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class UserEntity {

    @Id
    @Column(name = "user_id", columnDefinition = "uuid", nullable = false)
    private UUID userId;

    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    @Column(name = "second_name", length = 50)
    private String secondName;

    @Column(name = "last_name", length = 60)
    private String lastName;

    @Column(name = "second_last_name", length = 60)
    private String secondLastName;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", length = 20)
    private Gender gender;

    @Enumerated(EnumType.STRING)
    @Column(name = "credential_type", length = 20)
    private CredentialType credentialType;

    @Column(name = "credential_number", length = 64, unique = true)
    private String credentialNumber;

    @Column(name = "email", nullable = false, length = 255, unique = true)
    private String email;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "address", length = 100)
    private String address;

    @JsonIgnore
    @Column(name = "password_hash", nullable = false, columnDefinition = "bytea")
    private byte[] passwordHash;

    @Column(name = "failed_attempts")
    private Short failedAttempts;

    @Column(name = "locked_until")
    private LocalDateTime lockedUntil;

    @Column(name = "lock_reason", length = 500)
    private String lockReason;

    @Column(name = "primary_language", length = 10)
    private String primaryLanguage;

    @Column(name = "requires_sign_language")
    private Boolean requiresSignLanguage;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_account", nullable = false, length = 20)
    private AccountStatus statusAccount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_profile", length = 20)
    private ProfileStatus statusProfile;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", insertable = false)
    private LocalDateTime updatedAt;

    @PreUpdate
    void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}