package com.touristchain.backend.kyoto.controllers;

import com.touristchain.backend.kyoto.dto.DestinationRequest;
import com.touristchain.backend.kyoto.dto.DestinationResponse;
import com.touristchain.backend.kyoto.services.DestinationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/kyoto/destinations")
public class DestinationsController {

    @Autowired
    private DestinationService destinationService;

    // ========== 📋 CONSULTAS ==========

    @GetMapping
    public ResponseEntity<Page<DestinationResponse>> getDestinations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String country,
            @RequestParam(required = false) String category) {

        Page<DestinationResponse> destinations = destinationService.findWithFilters(
                country, category, page, size);
        return ResponseEntity.ok(destinations);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DestinationResponse> getDestination(@PathVariable Long id) {
        return destinationService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public ResponseEntity<List<DestinationResponse>> search(
            @RequestParam String query) {

        List<DestinationResponse> destinations = destinationService.search(query);
        return ResponseEntity.ok(destinations);
    }

    @GetMapping("/popular")
    public ResponseEntity<List<DestinationResponse>> getPopular(
            @RequestParam(defaultValue = "10") int limit) {

        List<DestinationResponse> destinations = destinationService.findPopular(limit);
        return ResponseEntity.ok(destinations);
    }

    @GetMapping("/country/{country}")
    public ResponseEntity<List<DestinationResponse>> getByCountry(
            @PathVariable String country) {

        List<DestinationResponse> destinations = destinationService.findByCountry(country);
        return ResponseEntity.ok(destinations);
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<DestinationResponse>> getByCategory(
            @PathVariable String category) {

        List<DestinationResponse> destinations = destinationService.findByCategory(category);
        return ResponseEntity.ok(destinations);
    }

    // ========== 📝 MUTACIONES ==========

    @PostMapping
    public ResponseEntity<DestinationResponse> createDestination(
            @Valid @RequestBody DestinationRequest request,
            @RequestHeader("X-Provider-Id") Long providerId) {

        try {
            DestinationResponse destination = destinationService.create(request, providerId);
            return ResponseEntity.ok(destination);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<DestinationResponse> updateDestination(
            @PathVariable Long id,
            @Valid @RequestBody DestinationRequest request) {

        try {
            DestinationResponse destination = destinationService.update(id, request);
            return ResponseEntity.ok(destination);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDestination(@PathVariable Long id) {
        try {
            destinationService.delete(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<DestinationResponse> activateDestination(@PathVariable Long id) {
        try {
            DestinationResponse destination = destinationService.activate(id);
            return ResponseEntity.ok(destination);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<DestinationResponse> deactivateDestination(@PathVariable Long id) {
        try {
            DestinationResponse destination = destinationService.deactivate(id);
            return ResponseEntity.ok(destination);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ========== 📊 ESTADÍSTICAS ==========

    @GetMapping("/stats")
    public ResponseEntity<?> getStats() {
        long total = destinationService.countAll();
        long active = destinationService.countActive();

        return ResponseEntity.ok(new Object() {
            public final long totalDestinations = total;
            public final long activeDestinations = active;
            public final long inactiveDestinations = total - active;
        });
    }
}