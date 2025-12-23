package com.touristchain.backend.kyoto.dto;

public record CatalogStatsResponse(
        long totalServices,
        long availableServices,
        long totalProviders,
        long servicesByHotel,
        long servicesByTour,
        long servicesByActivity,
        long servicesByRestaurant,
        long servicesByTransport) {
}