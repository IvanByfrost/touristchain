package com.touristchain.kyoto.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.touristchain.kyoto.dto.DestinationRequest.Coordinates;

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
                Coordinates coordinates,
                Double rating,
                Integer totalReviews,
                String contractAddress,
                String providerWallet,
                Boolean active,
                LocalDateTime createdAt,
                LocalDateTime updatedAt) {
}
