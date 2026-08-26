package com.touristchain.geneva.tourists;

import com.touristchain.geneva.security.UserPrincipal;
import com.touristchain.geneva.tourists.TouristService.*;
import com.touristchain.geneva.users.UserEntity;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/tourists")
@RequiredArgsConstructor
public class TouristController {

    private final TouristService touristService;

    /**
     * Crea un perfil de turista para el usuario autenticado.
     * POST /api/v1/tourists
     */
    @PostMapping
    public ResponseEntity<TouristEntity> createTouristProfile(
            @Valid @RequestBody CreateTouristRequest request) {
        UUID userId = getCurrentUserId();
        TouristEntity created = touristService.createTouristProfile(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Obtiene el perfil del turista autenticado.
     * GET /api/v1/tourists/me
     */
    @GetMapping("/me")
    public ResponseEntity<TouristEntity> getMyTouristProfile() {
        UUID userId = getCurrentUserId();
        TouristEntity tourist = touristService.getTouristByUserId(userId);
        return ResponseEntity.ok(tourist);
    }

    /**
     * Obtiene un perfil de turista por su ID (solo administradores).
     * GET /api/v1/tourists/{touristId}
     */
    @GetMapping("/{touristId}")
    public ResponseEntity<TouristEntity> getTouristById(@PathVariable UUID touristId) {
        // En un entorno real, se validaría que el usuario tenga rol ADMIN
        TouristEntity tourist = touristService.getTouristById(touristId);
        return ResponseEntity.ok(tourist);
    }

    /**
     * Obtiene un perfil de turista por ID de usuario (solo administradores).
     * GET /api/v1/tourists/user/{userId}
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<TouristEntity> getTouristByUserId(@PathVariable UUID userId) {
        TouristEntity tourist = touristService.getTouristByUserId(userId);
        return ResponseEntity.ok(tourist);
    }

    /**
     * Actualiza el perfil del turista autenticado.
     * PUT /api/v1/tourists/me
     */
    @PutMapping("/me")
    public ResponseEntity<TouristEntity> updateMyTouristProfile(
            @Valid @RequestBody UpdateTouristRequest request) {
        UUID userId = getCurrentUserId();
        TouristEntity tourist = touristService.getTouristByUserId(userId);
        TouristEntity updated = touristService.updateTouristProfile(tourist.getTouristId(), request);
        return ResponseEntity.ok(updated);
    }

    /**
     * Actualiza un perfil de turista por ID (solo administradores).
     * PUT /api/v1/tourists/{touristId}
     */
    @PutMapping("/{touristId}")
    public ResponseEntity<TouristEntity> updateTouristProfile(
            @PathVariable UUID touristId,
            @Valid @RequestBody UpdateTouristRequest request) {
        TouristEntity updated = touristService.updateTouristProfile(touristId, request);
        return ResponseEntity.ok(updated);
    }

    /**
     * Agrega puntos de fidelidad al turista autenticado.
     * POST /api/v1/tourists/me/loyalty-points
     */
    @PostMapping("/me/loyalty-points")
    public ResponseEntity<TouristEntity> addMyLoyaltyPoints(
            @Valid @RequestBody AddLoyaltyPointsRequest request) {
        UUID userId = getCurrentUserId();
        TouristEntity tourist = touristService.getTouristByUserId(userId);
        TouristEntity updated = touristService.addLoyaltyPoints(tourist.getTouristId(), request.pointsToAdd());
        return ResponseEntity.ok(updated);
    }

    /**
     * Agrega puntos de fidelidad a un turista (solo administradores).
     * POST /api/v1/tourists/{touristId}/loyalty-points
     */
    @PostMapping("/{touristId}/loyalty-points")
    public ResponseEntity<TouristEntity> addLoyaltyPoints(
            @PathVariable UUID touristId,
            @Valid @RequestBody AddLoyaltyPointsRequest request) {
        TouristEntity updated = touristService.addLoyaltyPoints(touristId, request.pointsToAdd());
        return ResponseEntity.ok(updated);
    }

    /**
     * Obtiene los puntos de fidelidad del turista autenticado.
     * GET /api/v1/tourists/me/loyalty-points
     */
    @GetMapping("/me/loyalty-points")
    public ResponseEntity<Integer> getMyLoyaltyPoints() {
        UUID userId = getCurrentUserId();
        TouristEntity tourist = touristService.getTouristByUserId(userId);
        Integer points = touristService.getLoyaltyPoints(tourist.getTouristId());
        return ResponseEntity.ok(points);
    }

    /**
     * Obtiene los puntos de fidelidad de un turista (solo administradores).
     * GET /api/v1/tourists/{touristId}/loyalty-points
     */
    @GetMapping("/{touristId}/loyalty-points")
    public ResponseEntity<Integer> getLoyaltyPoints(@PathVariable UUID touristId) {
        Integer points = touristService.getLoyaltyPoints(touristId);
        return ResponseEntity.ok(points);
    }

    /**
     * Obtiene todos los turistas suscritos a newsletters (solo administradores).
     * GET /api/v1/tourists/newsletter/subscribed
     */
    @GetMapping("/newsletter/subscribed")
    public ResponseEntity<List<TouristEntity>> getSubscribedToNewsletter() {
        List<TouristEntity> subscribed = touristService.getSubscribedToNewsletter();
        return ResponseEntity.ok(subscribed);
    }

    /**
     * Elimina el perfil del turista autenticado.
     * DELETE /api/v1/tourists/me
     */
    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteMyTouristProfile() {
        UUID userId = getCurrentUserId();
        TouristEntity tourist = touristService.getTouristByUserId(userId);
        touristService.deleteTouristProfile(tourist.getTouristId());
        return ResponseEntity.noContent().build();
    }

    /**
     * Elimina un perfil de turista (solo administradores).
     * DELETE /api/v1/tourists/{touristId}
     */
    @DeleteMapping("/{touristId}")
    public ResponseEntity<Void> deleteTouristProfile(@PathVariable UUID touristId) {
        touristService.deleteTouristProfile(touristId);
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