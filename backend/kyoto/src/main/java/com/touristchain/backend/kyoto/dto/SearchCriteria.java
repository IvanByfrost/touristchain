package com.touristchain.backend.kyoto.dto;

import java.math.BigDecimal;
import java.util.List;

import com.touristchain.backend.kyoto.models.ServiceType;

public record SearchCriteria(
        String query,
        ServiceType serviceType,
        String location,
        BigDecimal minPrice,
        BigDecimal maxPrice,
        BigDecimal minRating,
        List<String> tags,
        Boolean availableOnly,
        String sortBy, // "price_asc", "price_desc", "rating", "newest"
        Integer page,
        Integer size) {
    public SearchCriteria {
        if (availableOnly == null) {
            availableOnly = true;
        }
        if (page == null || page < 0) {
            page = 0;
        }
        if (size == null || size < 1) {
            size = 20;
        }
        if (size > 100) {
            size = 100; // Límite por seguridad
        }
    }
}