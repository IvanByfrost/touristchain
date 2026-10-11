package com.touristchain.geneva.auth;

import com.touristchain.geneva.users.UserEntity;
import com.touristchain.geneva.users.enums.CredentialType;
import com.touristchain.geneva.users.enums.Gender;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

record AuthResponse(String token, AuthUser user) {
}

record AuthUser(
        UUID id,
        String name,
        String firstName,
        String lastName,
        String email,
        String role,
        String status,
        String phone,
        String address,
        String location,
        String avatar,
        String bio,
        Integer loyaltyPoints,
        Map<String, Boolean> preferences,
        String createdAt,
        String lastLogin) {

    private static final Map<String, Boolean> DEFAULT_PREFERENCES = Map.of("email", true, "sms", false, "push", true);

    static AuthUser from(UserEntity user, String role, Integer loyaltyPoints) {
        String name = user.getLastName() == null
                ? user.getFirstName()
                : user.getFirstName() + " " + user.getLastName();
        return new AuthUser(
                user.getUserId(),
                name,
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                role,
                user.getStatusAccount().label(),
                user.getPhone(),
                user.getAddress(),
                user.getAddress(),
                "https://api.dicebear.com/7.x/avataaars/svg?seed="
                        + URLEncoder.encode(user.getEmail(), StandardCharsets.UTF_8),
                "",
                loyaltyPoints,
                DEFAULT_PREFERENCES,
                user.getCreatedAt() == null ? null : user.getCreatedAt().toLocalDate().toString(),
                null);
    }
}

record RegisterRequest(
        String name,
        String firstName,
        String secondName,
        String lastName,
        String secondLastName,
        String email,
        String password,
        String phone,
        String address,
        LocalDate dateOfBirth,
        Gender gender,
        String bloodType,
        CredentialType credentialType,
        String credentialNumber) {
}

record LoginRequest(String email, String password) {
}

record ProfileUpdateRequest(
        String name,
        String firstName,
        String lastName,
        String phone,
        String address,
        String location) {
}

record ChangePasswordRequest(String currentPassword, String newPassword) {
}
