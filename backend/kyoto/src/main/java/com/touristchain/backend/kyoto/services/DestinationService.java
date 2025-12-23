package com.touristchain.backend.kyoto.services;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.touristchain.backend.kyoto.dto.DestinationRequest;
import com.touristchain.backend.kyoto.dto.DestinationResponse;

public interface DestinationService {

    // Consultas básicas
    List<DestinationResponse> findAll();

    Page<DestinationResponse> findAll(Pageable pageable);

    Optional<DestinationResponse> findById(Long id);

    // Búsquedas y filtros
    Page<DestinationResponse> findWithFilters(String country, String category, int page, int size);

    List<DestinationResponse> findByCountry(String country);

    List<DestinationResponse> findByCategory(String category);

    List<DestinationResponse> search(String query);

    List<DestinationResponse> findPopular(int limit);

    List<DestinationResponse> findByPriceRange(Double minPrice, Double maxPrice);

    // Mutaciones
    DestinationResponse create(DestinationRequest request, Long providerId);

    DestinationResponse update(Long id, DestinationRequest request);

    DestinationResponse save(DestinationRequest request);

    void delete(Long id);

    DestinationResponse activate(Long id);

    DestinationResponse deactivate(Long id);

    List<DestinationResponse> searchByName(String name);

    // Estadísticas
    long countAll();

    long countActive();
}