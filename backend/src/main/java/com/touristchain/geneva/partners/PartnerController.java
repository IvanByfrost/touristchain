package com.touristchain.geneva.partners;

import com.touristchain.geneva.users.UserEntity;
import com.touristchain.geneva.partners.PartnerService.*;
import com.touristchain.geneva.security.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/partners")
@RequiredArgsConstructor
public class PartnerController {

    private final PartnerService partnerService;

    /**
     * Crea un perfil de partner para el usuario autenticado.
     * POST /api/v1/partners
     */
    @PostMapping
    public ResponseEntity<PartnerEntity> createPartnerProfile(
            @Valid @RequestBody CreatePartnerRequest request) {
        UUID userId = getCurrentUserId();
        PartnerEntity created = partnerService.createPartnerProfile(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Obtiene el perfil del partner autenticado.
     * GET /api/v1/partners/me
     */
    @GetMapping("/me")
    public ResponseEntity<PartnerEntity> getMyPartnerProfile() {
        UUID userId = getCurrentUserId();
        PartnerEntity partner = partnerService.getPartnerByUserId(userId);
        return ResponseEntity.ok(partner);
    }

    /**
     * Obtiene un partner por su ID (solo administradores).
     * GET /api/v1/partners/{partnerId}
     */
    @GetMapping("/{partnerId}")
    public ResponseEntity<PartnerEntity> getPartnerById(@PathVariable UUID partnerId) {
        PartnerEntity partner = partnerService.getPartnerById(partnerId);
        return ResponseEntity.ok(partner);
    }

    /**
     * Obtiene un partner por ID de usuario (solo administradores).
     * GET /api/v1/partners/user/{userId}
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<PartnerEntity> getPartnerByUserId(@PathVariable UUID userId) {
        PartnerEntity partner = partnerService.getPartnerByUserId(userId);
        return ResponseEntity.ok(partner);
    }

    /**
     * Actualiza el perfil del partner autenticado.
     * PUT /api/v1/partners/me
     */
    @PutMapping("/me")
    public ResponseEntity<PartnerEntity> updateMyPartnerProfile(
            @Valid @RequestBody UpdatePartnerRequest request) {
        UUID userId = getCurrentUserId();
        PartnerEntity partner = partnerService.getPartnerByUserId(userId);
        PartnerEntity updated = partnerService.updatePartnerProfile(partner.getPartnerId(), request);
        return ResponseEntity.ok(updated);
    }

    /**
     * Actualiza el perfil de un partner por ID (solo administradores).
     * PUT /api/v1/partners/{partnerId}
     */
    @PutMapping("/{partnerId}")
    public ResponseEntity<PartnerEntity> updatePartnerProfile(
            @PathVariable UUID partnerId,
            @Valid @RequestBody UpdatePartnerRequest request) {
        PartnerEntity updated = partnerService.updatePartnerProfile(partnerId, request);
        return ResponseEntity.ok(updated);
    }

    /**
     * Obtiene todos los partners (solo administradores).
     * GET /api/v1/partners/all
     */
    @GetMapping("/all")
    public ResponseEntity<List<PartnerEntity>> getAllPartners() {
        List<PartnerEntity> partners = partnerService.getAllPartners();
        return ResponseEntity.ok(partners);
    }

    /**
     * Obtiene partners por tipo de negocio.
     * GET /api/v1/partners/business-type/{businessType}
     */
    @GetMapping("/business-type/{businessType}")
    public ResponseEntity<List<PartnerEntity>> getPartnersByBusinessType(@PathVariable String businessType) {
        List<PartnerEntity> partners = partnerService.getPartnersByBusinessType(businessType);
        return ResponseEntity.ok(partners);
    }

    /**
     * Obtiene partners por país.
     * GET /api/v1/partners/country/{countryId}
     */
    @GetMapping("/country/{countryId}")
    public ResponseEntity<List<PartnerEntity>> getPartnersByCountry(@PathVariable UUID countryId) {
        List<PartnerEntity> partners = partnerService.getPartnersByCountry(countryId);
        return ResponseEntity.ok(partners);
    }

    /**
     * Busca partners por nombre.
     * GET /api/v1/partners/search?name=...
     */
    @GetMapping("/search")
    public ResponseEntity<List<PartnerEntity>> searchPartners(@RequestParam String name) {
        List<PartnerEntity> partners = partnerService.searchPartnersByName(name);
        return ResponseEntity.ok(partners);
    }

    /**
     * Obtiene los tipos de negocio disponibles.
     * GET /api/v1/partners/business-types
     */
    @GetMapping("/business-types")
    public ResponseEntity<List<String>> getBusinessTypes() {
        List<String> types = partnerService.getDistinctBusinessTypes();
        return ResponseEntity.ok(types);
    }

    /**
     * Obtiene estadísticas de partners por tipo de negocio.
     * GET /api/v1/partners/stats/business-types
     */
    @GetMapping("/stats/business-types")
    public ResponseEntity<Map<String, Long>> getPartnerStatsByBusinessType() {
        Map<String, Long> stats = partnerService.countPartnersByBusinessType();
        return ResponseEntity.ok(stats);
    }

    /**
     * Elimina el perfil del partner autenticado.
     * DELETE /api/v1/partners/me
     */
    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteMyPartnerProfile() {
        UUID userId = getCurrentUserId();
        PartnerEntity partner = partnerService.getPartnerByUserId(userId);
        partnerService.deletePartnerProfile(partner.getPartnerId());
        return ResponseEntity.noContent().build();
    }

    /**
     * Elimina un perfil de partner (solo administradores).
     * DELETE /api/v1/partners/{partnerId}
     */
    @DeleteMapping("/{partnerId}")
    public ResponseEntity<Void> deletePartnerProfile(@PathVariable UUID partnerId) {
        partnerService.deletePartnerProfile(partnerId);
        return ResponseEntity.noContent().build();
    }

    // ========== HELPER: Obtener userId del token JWT ==========

    private UUID getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("Usuario no autenticado");
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof UserEntity userEntity) {
            return userEntity.getUserId();
        } else if (principal instanceof UserPrincipal userPrincipal) {
            return userPrincipal.getUserId();
        } else {
            throw new IllegalStateException("No se pudo obtener el userId del token");
        }
    }
}