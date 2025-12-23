package com.touristchain.backend.ginebra.dto;

public record AuthResponse(
        String token,
        String tokenType,
        UserResponse user) {
}