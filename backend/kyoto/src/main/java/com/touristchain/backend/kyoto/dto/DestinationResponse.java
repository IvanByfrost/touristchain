package com.touristchain.backend.kyoto.dto;

import java.time.LocalDateTime;
import java.util.List;

public record DestinationResponse(
        Long id,
        String name,
        String description,
        String country,
        String city,
        String category,
        Double pricePerDay,
        String currency,
        List<String> images,
        List<String> tags,
        Double rating,
        Integer totalReviews,
        String contractAddress,
        String providerWallet,
        Boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}