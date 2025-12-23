package com.touristchain.backend.kyoto.controllers;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.touristchain.backend.kyoto.dto.CatalogStatsResponse;
import com.touristchain.backend.kyoto.dto.SearchCriteria;
import com.touristchain.backend.kyoto.dto.SearchResponse;
import com.touristchain.backend.kyoto.dto.TouristServiceRequest;
import com.touristchain.backend.kyoto.dto.TouristServiceResponse;
import com.touristchain.backend.kyoto.models.ServiceType;
import com.touristchain.backend.kyoto.services.CatalogService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/kyoto/services")
public class TouristServiceController {

    @Autowired
    private CatalogService catalogService;

    // ========== 📋 CONSULTAS GENERALES ==========

    @GetMapping
    public ResponseEntity<List<TouristServiceResponse>> getAllServices() {
        List<TouristServiceResponse> services = catalogService.getAllServices();
        return ResponseEntity.ok(services);
    }

    @GetMapping("/paginated")
    public ResponseEntity<Page<TouristServiceResponse>> getAllServicesPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {

        Sort sort = direction.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<TouristServiceResponse> services = catalogService.getAllServices(pageable);

        return ResponseEntity.ok(services);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TouristServiceResponse> getServiceById(@PathVariable Long id) {
        return catalogService.getServiceById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ========== 🔍 BÚSQUEDA Y FILTROS ==========

    @GetMapping("/search")
    public ResponseEntity<List<TouristServiceResponse>> searchServices(
            @RequestParam String query) {
        List<TouristServiceResponse> services = catalogService.searchServices(query);
        return ResponseEntity.ok(services);
    }

    @PostMapping("/search/advanced")
    public ResponseEntity<SearchResponse> advancedSearch(
            @RequestBody SearchCriteria criteria) {

        // Aplicar filtros
        List<TouristServiceResponse> services = catalogService.searchWithFilters(
                criteria.serviceType(),
                criteria.location(),
                criteria.minPrice(),
                criteria.maxPrice());

        // Aplicar ordenamiento
        services = applySorting(services, criteria.sortBy());

        // Aplicar paginación
        int start = criteria.page() * criteria.size();
        int end = Math.min(start + criteria.size(), services.size());

        if (start > services.size()) {
            return ResponseEntity.ok(new SearchResponse(
                    List.of(), criteria.page(), 0, services.size(), false, false));
        }

        List<TouristServiceResponse> pagedServices = services.subList(start, end);
        int totalPages = (int) Math.ceil((double) services.size() / criteria.size());

        return ResponseEntity.ok(new SearchResponse(
                pagedServices,
                criteria.page(),
                totalPages,
                services.size(),
                criteria.page() < totalPages - 1,
                criteria.page() > 0));
    }

    @GetMapping("/type/{serviceType}")
    public ResponseEntity<List<TouristServiceResponse>> getServicesByType(
            @PathVariable ServiceType serviceType) {
        List<TouristServiceResponse> services = catalogService.getServicesByType(serviceType);
        return ResponseEntity.ok(services);
    }

    @GetMapping("/location/{location}")
    public ResponseEntity<List<TouristServiceResponse>> getServicesByLocation(
            @PathVariable String location) {
        List<TouristServiceResponse> services = catalogService.getServicesByLocation(location);
        return ResponseEntity.ok(services);
    }

    @GetMapping("/available")
    public ResponseEntity<List<TouristServiceResponse>> getAvailableServices() {
        List<TouristServiceResponse> services = catalogService.getAvailableServices();
        return ResponseEntity.ok(services);
    }

    @GetMapping("/provider/{providerId}")
    public ResponseEntity<List<TouristServiceResponse>> getServicesByProvider(
            @PathVariable Long providerId) {
        List<TouristServiceResponse> services = catalogService.getServicesByProvider(providerId);
        return ResponseEntity.ok(services);
    }

    // ========== 📝 MUTACIONES (CRUD) ==========

    @PostMapping
    public ResponseEntity<TouristServiceResponse> createService(
            @Valid @RequestBody TouristServiceRequest request,
            @RequestHeader("X-Provider-Id") Long providerId) {

        try {
            TouristServiceResponse newService = catalogService.createService(request, providerId);
            return ResponseEntity.ok(newService);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<TouristServiceResponse> updateService(
            @PathVariable Long id,
            @Valid @RequestBody TouristServiceRequest request) {

        try {
            TouristServiceResponse updatedService = catalogService.updateService(id, request);
            return ResponseEntity.ok(updatedService);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteService(@PathVariable Long id) {
        try {
            catalogService.deleteService(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/{id}/availability")
    public ResponseEntity<TouristServiceResponse> toggleAvailability(@PathVariable Long id) {
        try {
            TouristServiceResponse updatedService = catalogService.toggleAvailability(id);
            return ResponseEntity.ok(updatedService);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{id}/reviews")
    public ResponseEntity<TouristServiceResponse> addReview(
            @PathVariable Long id,
            @RequestParam BigDecimal rating) {

        try {
            TouristServiceResponse updatedService = catalogService.addReview(id, rating);
            return ResponseEntity.ok(updatedService);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // ========== 📊 ESTADÍSTICAS ==========

    @GetMapping("/stats")
    public ResponseEntity<CatalogStatsResponse> getCatalogStats() {
        long totalServices = catalogService.countServices();
        long availableServices = catalogService.countAvailableServices();

        // Estos valores vendrían de consultas más específicas
        // Por ahora valores de ejemplo
        return ResponseEntity.ok(new CatalogStatsResponse(
                totalServices,
                availableServices,
                0, // totalProviders - necesitaría integración con Ginebra
                0, // servicesByHotel
                0, // servicesByTour
                0, // servicesByActivity
                0, // servicesByRestaurant
                0 // servicesByTransport
        ));
    }

    @GetMapping("/stats/provider/{providerId}")
    public ResponseEntity<?> getProviderStats(@PathVariable Long providerId) {
        long totalServices = catalogService.countServicesByProvider(providerId);

        return ResponseEntity.ok(new Object() {
            public final long id = providerId; // ← Nombre diferente
            public final long total = totalServices;
        });
    }

    // ========== 🔧 MÉTODOS AUXILIARES ==========

    private List<TouristServiceResponse> applySorting(
            List<TouristServiceResponse> services, String sortBy) {

        if (sortBy == null) {
            return services;
        }

        return switch (sortBy.toLowerCase()) {
            case "price_asc" -> services.stream()
                    .sorted((a, b) -> a.price().compareTo(b.price()))
                    .toList();

            case "price_desc" -> services.stream()
                    .sorted((a, b) -> b.price().compareTo(a.price()))
                    .toList();

            case "rating" -> services.stream()
                    .sorted((a, b) -> b.rating().compareTo(a.rating()))
                    .toList();

            case "newest" -> services.stream()
                    .sorted((a, b) -> b.createdAt().compareTo(a.createdAt()))
                    .toList();

            default -> services;
        };
    }
}