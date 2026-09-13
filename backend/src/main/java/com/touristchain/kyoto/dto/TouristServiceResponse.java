package com.touristchain.kyoto.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record TouristServiceResponse(
                Long id,
                Long providerId,
                String name,
                ServiceType serviceType,
                String description,
                BigDecimal price,
                String currency,
                String location,
                String detailedAddress,
                Boolean available,
                Integer maxCapacity,
                Integer currentAvailability,
                List<String> images,
                List<String> tags,
                BigDecimal rating,
                Integer totalReviews,
                String contractAddress,
                Integer durationDays,
                Integer durationHours,
                LocalDateTime createdAt,
                LocalDateTime updatedAt) {
}