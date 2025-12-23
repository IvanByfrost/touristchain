package com.touristchain.backend.kyoto.services;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.touristchain.backend.kyoto.dto.TouristServiceRequest;
import com.touristchain.backend.kyoto.dto.TouristServiceResponse;
import com.touristchain.backend.kyoto.models.ServiceType;
import com.touristchain.backend.kyoto.models.entities.TouristService;
import com.touristchain.backend.kyoto.repositories.TouristServiceRepository;

@Service
@Transactional
public class CatalogService {

    @Autowired
    private TouristServiceRepository touristServiceRepository;

    // ========== CONSULTAS ==========

    @Transactional(readOnly = true)
    public List<TouristServiceResponse> getAllServices() {
        return touristServiceRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<TouristServiceResponse> getAllServices(Pageable pageable) {
        return touristServiceRepository.findAll(pageable)
                .map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public Optional<TouristServiceResponse> getServiceById(Long id) {
        return touristServiceRepository.findById(id)
                .map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public List<TouristServiceResponse> getServicesByProvider(Long providerId) {
        return touristServiceRepository.findByProviderId(providerId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TouristServiceResponse> getServicesByType(ServiceType serviceType) {
        return touristServiceRepository.findByServiceType(serviceType)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TouristServiceResponse> getServicesByLocation(String location) {
        return touristServiceRepository.findByLocation(location)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TouristServiceResponse> getAvailableServices() {
        return touristServiceRepository.findByAvailableTrue()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TouristServiceResponse> searchServices(String query) {
        return touristServiceRepository.searchAvailableByText(query)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TouristServiceResponse> searchWithFilters(
            ServiceType type,
            String location,
            BigDecimal minPrice,
            BigDecimal maxPrice) {
        return touristServiceRepository.searchWithFilters(type, location, minPrice, maxPrice)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ========== MUTACIONES ==========

    public TouristServiceResponse createService(TouristServiceRequest request, Long providerId) {
        // Validar que el proveedor existe (debería llamar a Ginebra)
        // Por ahora solo asignamos el ID

        TouristService service = new TouristService();
        service.setProviderId(providerId);
        service.setName(request.name());
        service.setServiceType(request.serviceType());
        service.setDescription(request.description());
        service.setPrice(request.price());
        service.setCurrency(request.currency());
        service.setLocation(request.location());
        service.setDetailedAddress(request.detailedAddress());
        service.setAvailable(request.available());
        service.setMaxCapacity(request.maxCapacity());
        service.setCurrentAvailability(request.currentAvailability());
        service.setContractAddress(request.contractAddress());
        service.setDurationDays(request.durationDays());
        service.setDurationHours(request.durationHours());

        if (request.images() != null) {
            service.setImages(request.images());
        }

        if (request.tags() != null) {
            service.setTags(request.tags());
        }

        TouristService savedService = touristServiceRepository.save(service);
        return toResponse(savedService);
    }

    public TouristServiceResponse updateService(Long id, TouristServiceRequest request) {
        TouristService service = touristServiceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Servicio no encontrado"));

        // Actualizar campos permitidos
        if (request.name() != null) {
            service.setName(request.name());
        }
        if (request.serviceType() != null) {
            service.setServiceType(request.serviceType());
        }
        if (request.description() != null) {
            service.setDescription(request.description());
        }
        if (request.price() != null) {
            service.setPrice(request.price());
        }
        if (request.currency() != null) {
            service.setCurrency(request.currency());
        }
        if (request.location() != null) {
            service.setLocation(request.location());
        }
        if (request.detailedAddress() != null) {
            service.setDetailedAddress(request.detailedAddress());
        }
        if (request.available() != null) {
            service.setAvailable(request.available());
        }
        if (request.maxCapacity() != null) {
            service.setMaxCapacity(request.maxCapacity());
        }
        if (request.currentAvailability() != null) {
            service.setCurrentAvailability(request.currentAvailability());
        }
        if (request.contractAddress() != null) {
            service.setContractAddress(request.contractAddress());
        }
        if (request.durationDays() != null) {
            service.setDurationDays(request.durationDays());
        }
        if (request.durationHours() != null) {
            service.setDurationHours(request.durationHours());
        }
        if (request.images() != null) {
            service.setImages(request.images());
        }
        if (request.tags() != null) {
            service.setTags(request.tags());
        }

        service.setUpdatedAt(LocalDateTime.now());
        TouristService updatedService = touristServiceRepository.save(service);
        return toResponse(updatedService);
    }

    public void deleteService(Long id) {
        if (!touristServiceRepository.existsById(id)) {
            throw new RuntimeException("Servicio no encontrado");
        }
        touristServiceRepository.deleteById(id);
    }

    public TouristServiceResponse toggleAvailability(Long id) {
        TouristService service = touristServiceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Servicio no encontrado"));

        service.setAvailable(!service.getAvailable());
        service.setUpdatedAt(LocalDateTime.now());

        TouristService updatedService = touristServiceRepository.save(service);
        return toResponse(updatedService);
    }

    public TouristServiceResponse addReview(Long id, BigDecimal rating) {
        TouristService service = touristServiceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Servicio no encontrado"));

        service.addReview(rating);
        service.setUpdatedAt(LocalDateTime.now());

        TouristService updatedService = touristServiceRepository.save(service);
        return toResponse(updatedService);
    }

    // ========== UTILIDADES ==========

    private TouristServiceResponse toResponse(TouristService service) {
        return new TouristServiceResponse(
                service.getId(),
                service.getProviderId(),
                service.getName(),
                service.getServiceType(),
                service.getDescription(),
                service.getPrice(),
                service.getCurrency(),
                service.getLocation(),
                service.getDetailedAddress(),
                service.getAvailable(),
                service.getMaxCapacity(),
                service.getCurrentAvailability(),
                service.getImages(),
                service.getTags(),
                service.getRating(),
                service.getTotalReviews(),
                service.getContractAddress(),
                service.getDurationDays(),
                service.getDurationHours(),
                service.getCreatedAt(),
                service.getUpdatedAt());
    }

    @Transactional(readOnly = true)
    public long countServices() {
        return touristServiceRepository.count();
    }

    @Transactional(readOnly = true)
    public long countServicesByProvider(Long providerId) {
        return touristServiceRepository.countByProviderId(providerId);
    }

    @Transactional(readOnly = true)
    public long countAvailableServices() {
        return touristServiceRepository.findByAvailableTrue().size();
    }
}