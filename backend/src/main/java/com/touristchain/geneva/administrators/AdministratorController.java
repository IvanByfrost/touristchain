package com.touristchain.geneva.administrators;

import com.touristchain.geneva.administrators.AdministratorEntity.AdminLevel;
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
@RequestMapping("/api/v1/administrators")
@RequiredArgsConstructor
public class AdministratorController {

    private final AdministratorService administratorService;

    /**
     * Crea un perfil de administrador (solo SUPERADMIN).
     * POST /api/v1/administrators
     */
    @PostMapping
    public ResponseEntity<AdministratorEntity> createAdministratorProfile(
            @Valid @RequestBody CreateAdministratorRequest request) {
        // Solo SUPERADMIN puede crear administradores
        UUID currentUserId = getCurrentUserId();
        if (!administratorService.hasAdminLevel(currentUserId, AdminLevel.SUPERADMIN)) {
            throw new SecurityException("Se requiere rol SUPERADMIN para crear administradores");
        }

        AdministratorEntity created = administratorService.createAdministratorProfile(
                request.userId(),
                request.adminLevel());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Obtiene el perfil del administrador autenticado.
     * GET /api/v1/administrators/me
     */
    @GetMapping("/me")
    public ResponseEntity<AdministratorEntity> getMyAdministratorProfile() {
        UUID userId = getCurrentUserId();
        AdministratorEntity administrator = administratorService.getAdministratorByUserId(userId);
        return ResponseEntity.ok(administrator);
    }

    /**
     * Obtiene un perfil de administrador por ID.
     * GET /api/v1/administrators/{administratorId}
     */
    @GetMapping("/{administratorId}")
    public ResponseEntity<AdministratorEntity> getAdministratorById(@PathVariable UUID administratorId) {
        AdministratorEntity administrator = administratorService.getAdministratorById(administratorId);
        return ResponseEntity.ok(administrator);
    }

    /**
     * Obtiene un perfil de administrador por ID de usuario.
     * GET /api/v1/administrators/user/{userId}
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<AdministratorEntity> getAdministratorByUserId(@PathVariable UUID userId) {
        AdministratorEntity administrator = administratorService.getAdministratorByUserId(userId);
        return ResponseEntity.ok(administrator);
    }

    /**
     * Actualiza el nivel de administrador (solo SUPERADMIN).
     * PUT /api/v1/administrators/{administratorId}/level
     */
    @PutMapping("/{administratorId}/level")
    public ResponseEntity<AdministratorEntity> updateAdminLevel(
            @PathVariable UUID administratorId,
            @Valid @RequestBody UpdateAdminLevelRequest request) {
        // Solo SUPERADMIN puede cambiar niveles
        UUID currentUserId = getCurrentUserId();
        if (!administratorService.hasAdminLevel(currentUserId, AdminLevel.SUPERADMIN)) {
            throw new SecurityException("Se requiere rol SUPERADMIN para cambiar niveles de administrador");
        }

        AdministratorEntity updated = administratorService.updateAdminLevel(administratorId, request.adminLevel());
        return ResponseEntity.ok(updated);
    }

    /**
     * Verifica si el usuario autenticado es administrador.
     * GET /api/v1/administrators/me/is-admin
     */
    @GetMapping("/me/is-admin")
    public ResponseEntity<Boolean> isMyAdministrator() {
        UUID userId = getCurrentUserId();
        boolean isAdmin = administratorService.isAdministrator(userId);
        return ResponseEntity.ok(isAdmin);
    }

    /**
     * Verifica si un usuario es administrador.
     * GET /api/v1/administrators/{userId}/is-admin
     */
    @GetMapping("/{userId}/is-admin")
    public ResponseEntity<Boolean> isAdministrator(@PathVariable UUID userId) {
        boolean isAdmin = administratorService.isAdministrator(userId);
        return ResponseEntity.ok(isAdmin);
    }

    /**
     * Obtiene el nivel del administrador autenticado.
     * GET /api/v1/administrators/me/level
     */
    @GetMapping("/me/level")
    public ResponseEntity<AdminLevel> getMyAdminLevel() {
        UUID userId = getCurrentUserId();
        AdminLevel level = administratorService.getAdminLevel(userId);
        return ResponseEntity.ok(level);
    }

    /**
     * Obtiene el nivel de administrador de un usuario.
     * GET /api/v1/administrators/{userId}/level
     */
    @GetMapping("/{userId}/level")
    public ResponseEntity<AdminLevel> getAdminLevel(@PathVariable UUID userId) {
        AdminLevel level = administratorService.getAdminLevel(userId);
        return ResponseEntity.ok(level);
    }

    /**
     * Obtiene todos los administradores (solo SUPERADMIN).
     * GET /api/v1/administrators/all
     */
    @GetMapping("/all")
    public ResponseEntity<List<AdministratorEntity>> getAllAdministrators() {
        // Solo SUPERADMIN puede listar todos
        UUID currentUserId = getCurrentUserId();
        if (!administratorService.hasAdminLevel(currentUserId, AdminLevel.SUPERADMIN)) {
            throw new SecurityException("Se requiere rol SUPERADMIN para listar todos los administradores");
        }

        List<AdministratorEntity> administrators = administratorService.getAllAdministrators();
        return ResponseEntity.ok(administrators);
    }

    /**
     * Elimina el perfil del administrador autenticado.
     * DELETE /api/v1/administrators/me
     */
    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteMyAdministratorProfile() {
        UUID userId = getCurrentUserId();
        AdministratorEntity administrator = administratorService.getAdministratorByUserId(userId);
        administratorService.deleteAdministratorProfile(administrator.getAdministratorId());
        return ResponseEntity.noContent().build();
    }

    /**
     * Elimina un perfil de administrador (solo SUPERADMIN).
     * DELETE /api/v1/administrators/{administratorId}
     */
    @DeleteMapping("/{administratorId}")
    public ResponseEntity<Void> deleteAdministratorProfile(@PathVariable UUID administratorId) {
        // Solo SUPERADMIN puede eliminar administradores
        UUID currentUserId = getCurrentUserId();
        if (!administratorService.hasAdminLevel(currentUserId, AdminLevel.SUPERADMIN)) {
            throw new SecurityException("Se requiere rol SUPERADMIN para eliminar administradores");
        }

        administratorService.deleteAdministratorProfile(administratorId);
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
        } else if (principal instanceof com.touristchain.geneva.security.UserPrincipal userPrincipal) {
            return userPrincipal.getUserId();
        } else {
            throw new IllegalStateException("No se pudo obtener el userId del token");
        }
    }

    public record UpdateAdminLevelRequest(
            AdminLevel adminLevel) {
    }

    public record CreateAdministratorRequest(
            UUID userId,
            AdminLevel adminLevel) {
    }
}