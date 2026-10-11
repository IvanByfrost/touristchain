package com.touristchain.kyoto.destination;

import com.touristchain.kyoto.dto.DestinationRequest;
import com.touristchain.kyoto.dto.DestinationResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Destinos: lectura pública (solo activos), escritura solo administradores (ver SecurityConfig).
 * Un administrador autenticado puede ver también los inactivos.
 */
@RestController
@RequestMapping("/api/v1/kyoto/destinations")
@RequiredArgsConstructor
public class DestinationsController {

    private final DestinationService destinationService;

    // ========== 📋 CONSULTAS ==========

    @GetMapping
    public Page<DestinationResponse> getDestinations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String country,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "false") boolean includeInactive) {
        return destinationService.findWithFilters(country, city, category,
                includeInactive && isAdmin(), page, size);
    }

    @GetMapping("/{id}")
    public DestinationResponse getDestination(@PathVariable Long id) {
        return destinationService.findById(id, isAdmin());
    }

    @GetMapping("/search")
    public List<DestinationResponse> search(@RequestParam String query) {
        return destinationService.search(query);
    }

    @GetMapping("/popular")
    public List<DestinationResponse> getPopular(@RequestParam(defaultValue = "10") int limit) {
        return destinationService.findPopular(limit);
    }

    @GetMapping("/country/{country}")
    public List<DestinationResponse> getByCountry(@PathVariable String country) {
        return destinationService.findAll(country, null);
    }

    @GetMapping("/category/{category}")
    public List<DestinationResponse> getByCategory(@PathVariable String category) {
        return destinationService.findAll(null, category);
    }

    // ========== 📝 MUTACIONES ==========

    @PostMapping
    public ResponseEntity<DestinationResponse> createDestination(@Valid @RequestBody DestinationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(destinationService.create(request));
    }

    @PutMapping("/{id}")
    public DestinationResponse updateDestination(@PathVariable Long id,
                                                 @Valid @RequestBody DestinationRequest request) {
        return destinationService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public Map<String, String> deleteDestination(@PathVariable Long id) {
        destinationService.delete(id);
        return Map.of("message", "Destino eliminado");
    }

    @PatchMapping("/{id}/activate")
    public DestinationResponse activateDestination(@PathVariable Long id) {
        return destinationService.setActive(id, true);
    }

    @PatchMapping("/{id}/deactivate")
    public DestinationResponse deactivateDestination(@PathVariable Long id) {
        return destinationService.setActive(id, false);
    }

    // ========== 📊 ESTADÍSTICAS ==========

    @GetMapping("/stats")
    public Map<String, Long> getStats() {
        long total = destinationService.countAll();
        long active = destinationService.countActive();
        return Map.of(
                "totalDestinations", total,
                "activeDestinations", active,
                "inactiveDestinations", total - active);
    }

    private static boolean isAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_ADMIN".equals(authority.getAuthority()));
    }
}
