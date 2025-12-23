package com.touristchain.backend.ginebra.dto;

import java.time.LocalDateTime;

import com.touristchain.backend.ginebra.models.BusinessType;

public record ProviderResponse(
        Long id,
        Long userId,
        String companyName,
        BusinessType businessType,
        String taxId,
        String licenseNumber,
        String businessAddress,
        Boolean verified,
        Double overallRating,
        Integer totalReviews,
        LocalDateTime createdAt,
        LocalDateTime verifiedAt) {
}