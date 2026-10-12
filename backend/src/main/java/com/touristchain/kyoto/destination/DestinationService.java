package com.touristchain.kyoto.destination;

import com.touristchain.common.NotFoundException;
import com.touristchain.kyoto.dto.DestinationRequest;
import com.touristchain.kyoto.dto.DestinationRequest.Coordinates;
import com.touristchain.kyoto.dto.DestinationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DestinationService {

    private final DestinationRepository destinationRepository;

    /** Los destinos inactivos solo se incluyen si {@code includeInactive} (lo decide el controlador). */
    @Transactional(readOnly = true)
    public Page<DestinationResponse> findWithFilters(String country, String city, String category,
                                                     boolean includeInactive, int page, int size) {
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 100), Sort.by("id"));
        return destinationRepository.findWithFilters(
                        trimToEmpty(country), trimToEmpty(city), trimToEmpty(category), includeInactive, pageable)
                .map(DestinationService::toResponse);
    }

    @Transactional(readOnly = true)
    public List<DestinationResponse> findAll(String country, String category) {
        return destinationRepository.findWithFilters(
                        trimToEmpty(country), "", trimToEmpty(category), false, Pageable.unpaged(Sort.by("id")))
                .map(DestinationService::toResponse)
                .getContent();
    }

    @Transactional(readOnly = true)
    public DestinationResponse findById(Long id, boolean includeInactive) {
        Destination destination = find(id);
        if (!includeInactive && !destination.getActive()) {
            throw new NotFoundException("Destino no encontrado");
        }
        return toResponse(destination);
    }

    @Transactional(readOnly = true)
    public List<DestinationResponse> search(String query) {
        return destinationRepository.searchActive(query.trim()).stream().map(DestinationService::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<DestinationResponse> findPopular(int limit) {
        return destinationRepository.findByActiveTrueOrderByRatingDescTotalReviewsDesc(
                        PageRequest.of(0, Math.min(Math.max(limit, 1), 100)))
                .map(DestinationService::toResponse)
                .getContent();
    }

    @Transactional
    public DestinationResponse create(DestinationRequest request) {
        Destination destination = new Destination();
        apply(destination, request);
        return toResponse(destinationRepository.save(destination));
    }

    @Transactional
    public DestinationResponse update(Long id, DestinationRequest request) {
        Destination destination = find(id);
        apply(destination, request);
        return toResponse(destinationRepository.saveAndFlush(destination));
    }

    /** Los viajes que apuntaban a este destino quedan sin destino (ON DELETE SET NULL). */
    @Transactional
    public void delete(Long id) {
        destinationRepository.delete(find(id));
    }

    @Transactional
    public DestinationResponse setActive(Long id, boolean active) {
        Destination destination = find(id);
        destination.setActive(active);
        return toResponse(destinationRepository.saveAndFlush(destination));
    }

    /** Para el módulo de viajes: el destino debe existir y estar activo. */
    @Transactional(readOnly = true)
    public void requireActive(Long id) {
        Destination destination = destinationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("El destino seleccionado no existe"));
        if (!destination.getActive()) {
            throw new IllegalArgumentException("El destino está desactivado");
        }
    }

    @Transactional(readOnly = true)
    public long countAll() {
        return destinationRepository.count();
    }

    @Transactional(readOnly = true)
    public long countActive() {
        return destinationRepository.countByActiveTrue();
    }

    private Destination find(Long id) {
        return destinationRepository.findById(id).orElseThrow(() -> new NotFoundException("Destino no encontrado"));
    }

    private static void apply(Destination destination, DestinationRequest request) {
        destination.setName(request.name().trim());
        destination.setDescription(request.description());
        destination.setCountry(request.country().trim());
        destination.setCity(request.city().trim());
        destination.setCategory(request.category().trim().toUpperCase());
        destination.setPricePerDay(request.pricePerDay());
        destination.setCurrency(request.currency());
        if (request.images() != null) {
            destination.setImages(new ArrayList<>(request.images()));
        }
        if (request.tags() != null) {
            destination.setTags(new ArrayList<>(request.tags()));
        }
        if (request.coordinates() != null) {
            destination.setLatitude(request.coordinates().lat());
            destination.setLongitude(request.coordinates().lng());
        }
        if (request.providerWallet() != null) {
            destination.setProviderWallet(request.providerWallet());
        }
        if (request.contractAddress() != null) {
            destination.setContractAddress(request.contractAddress());
        }
    }

    private static DestinationResponse toResponse(Destination destination) {
        Coordinates coordinates = destination.getLatitude() == null || destination.getLongitude() == null ? null
                : new Coordinates(destination.getLatitude(), destination.getLongitude());
        return new DestinationResponse(
                destination.getId(),
                destination.getName(),
                destination.getDescription(),
                destination.getCountry(),
                destination.getCity(),
                destination.getCategory(),
                destination.getPricePerDay(),
                destination.getCurrency(),
                new ArrayList<>(destination.getImages()),
                new ArrayList<>(destination.getTags()),
                coordinates,
                destination.getRating(),
                destination.getTotalReviews(),
                destination.getContractAddress(),
                destination.getProviderWallet(),
                destination.getActive(),
                destination.getCreatedAt(),
                destination.getUpdatedAt());
    }

    private static String trimToEmpty(String value) {
        return value == null ? "" : value.trim();
    }
}
