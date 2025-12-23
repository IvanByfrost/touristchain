package com.touristchain.backend.ginebra.dto;

import java.time.LocalDateTime;

import com.touristchain.backend.ginebra.models.entities.UserType;

public record UserResponse(
        Long id,
        String username,
        String email,
        String walletAddress,
        UserType userType,
        boolean verified,
        boolean active,
        LocalDateTime createdAt,
        LocalDateTime lastLogin) {
}