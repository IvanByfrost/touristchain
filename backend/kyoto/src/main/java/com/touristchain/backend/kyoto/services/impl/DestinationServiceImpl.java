package com.touristchain.backend.kyoto.services.impl;

import com.touristchain.backend.kyoto.dto.DestinationRequest;
import com.touristchain.backend.kyoto.dto.DestinationResponse;
import com.touristchain.backend.kyoto.models.entities.Destination;
import com.touristchain.backend.kyoto.repositories.DestinationRepository;
import com.touristchain.backend.kyoto.services.DestinationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class DestinationServiceImpl implements DestinationService {

    @Autowired
    private DestinationRepository destinationRepository;

    @Override
    @Transactional(readOnly = true)
    public List<DestinationResponse> findAll() {
        return destinationRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DestinationResponse> findAll(Pageable pageable) {
        return destinationRepository.findAll(pageable)
                .map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<DestinationResponse> findById(Long id) {
        return destinationRepository.findById(id)
                .map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DestinationResponse> findByCountry(String country) {
        return destinationRepository.findByCountry(country)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DestinationResponse> findByCategory(String category) {
        return destinationRepository.findByCategory(category)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DestinationResponse> searchByName(String name) {
        return destinationRepository.findByNameContainingIgnoreCase(name)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DestinationResponse> findByPriceRange(Double minPrice, Double maxPrice) {
        return destinationRepository.findByPricePerDayBetween(minPrice, maxPrice)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DestinationResponse> findPopular(int limit) {
        return destinationRepository.findByActiveTrueOrderByRatingDesc()
                .stream()
                .limit(limit)
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // En DestinationServiceImpl
    @Transactional(readOnly = true)
    public List<DestinationResponse> findWithFilters(
            String country, String category, Double minPrice, Double maxPrice) {

        // Usar el método existente con Pageable sin límite
        Pageable pageable = Pageable.unpaged();

        Page<Destination> destinations = destinationRepository.findWithFilters(
                country,
                null, // city (no se usa)
                category,
                minPrice,
                maxPrice,
                null, // minRating (no se usa)
                pageable);

        return destinations.getContent()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public DestinationResponse save(DestinationRequest request) {
        Destination destination = new Destination();
        updateDestinationFromRequest(destination, request);

        Destination savedDestination = destinationRepository.save(destination);
        return toResponse(savedDestination);
    }

    @Override
    public DestinationResponse update(Long id, DestinationRequest request) {
        Destination destination = destinationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Destino no encontrado"));

        updateDestinationFromRequest(destination, request);
        destination.setUpdatedAt(LocalDateTime.now());

        Destination updatedDestination = destinationRepository.save(destination);
        return toResponse(updatedDestination);
    }

    @Override
    public void delete(Long id) {
        if (!destinationRepository.existsById(id)) {
            throw new RuntimeException("Destino no encontrado");
        }
        destinationRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public long countAll() {
        return destinationRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public long countActive() {
        return destinationRepository.countByActiveTrue();
    }

    // En DestinationServiceImpl, añade estos métodos:

    @Override
    @Transactional(readOnly = true)
    public Page<DestinationResponse> findWithFilters(String country, String category, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        // Usa el método del repository que soporta paginación
        // Si no existe, crea uno o adapta
        return destinationRepository.findAll(pageable) // Temporal
                .map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DestinationResponse> search(String query) {
        // Usa el método fullTextSearch del repository
        return destinationRepository.fullTextSearch(query)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public DestinationResponse create(DestinationRequest request, Long providerId) {
        Destination destination = new Destination();
        updateDestinationFromRequest(destination, request);
        destination.setActive(true);

        // Aquí podrías validar providerId con Ginebra

        Destination savedDestination = destinationRepository.save(destination);
        return toResponse(savedDestination);
    }

    @Override
    public DestinationResponse activate(Long id) {
        Destination destination = destinationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Destino no encontrado"));

        destination.setActive(true);
        destination.setUpdatedAt(LocalDateTime.now());

        return toResponse(destinationRepository.save(destination));
    }

    @Override
    public DestinationResponse deactivate(Long id) {
        Destination destination = destinationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Destino no encontrado"));

        destination.setActive(false);
        destination.setUpdatedAt(LocalDateTime.now());

        return toResponse(destinationRepository.save(destination));
    }

    // ========== MÉTODOS PRIVADOS ==========

    private void updateDestinationFromRequest(Destination destination, DestinationRequest request) {
        destination.setName(request.name());
        destination.setDescription(request.description());
        destination.setCountry(request.country());
        destination.setCity(request.city());
        destination.setCategory(request.category());
        destination.setPricePerDay(request.pricePerDay());
        destination.setCurrency(request.currency());

        if (request.images() != null) {
            destination.setImages(request.images());
        }

        if (request.tags() != null) {
            destination.setTags(request.tags());
        }

        if (request.providerWallet() != null) {
            destination.setProviderWallet(request.providerWallet());
        }

        if (request.contractAddress() != null) {
            destination.setContractAddress(request.contractAddress());
        }

        // Por defecto activo al crear
        if (destination.getId() == null) {
            destination.setActive(true);
        }
    }

    private DestinationResponse toResponse(Destination destination) {
        return new DestinationResponse(
                destination.getId(),
                destination.getName(),
                destination.getDescription(),
                destination.getCountry(),
                destination.getCity(),
                destination.getCategory(),
                destination.getPricePerDay(),
                destination.getCurrency(),
                destination.getImages(),
                destination.getTags(),
                destination.getRating(),
                destination.getTotalReviews(),
                destination.getContractAddress(),
                destination.getProviderWallet(),
                destination.getActive(),
                destination.getCreatedAt(),
                destination.getUpdatedAt());
    }
}