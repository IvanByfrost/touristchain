package com.touristchain.backend.kyoto.dto;

import java.util.List;

public record SearchResponse(
        List<TouristServiceResponse> services,
        int currentPage,
        int totalPages,
        long totalElements,
        boolean hasNext,
        boolean hasPrevious) {
}