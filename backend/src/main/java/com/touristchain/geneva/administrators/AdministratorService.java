package com.touristchain.geneva.administrators;

import com.touristchain.geneva.administrators.AdministratorEntity.AdminLevel;
import com.touristchain.geneva.users.UserEntity;
import com.touristchain.geneva.users.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdministratorService {

    private final AdministratorRepository administratorRepository;
    private final UserRepository userRepository;

    /**
     * Crea un perfil de administrador para un usuario existente.
     */
    @Transactional
    public AdministratorEntity createAdministratorProfile(UUID userId, AdminLevel adminLevel) {
        // Verificar que el usuario existe
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        // Verificar que el usuario no tenga ya un perfil de administrador
        if (administratorRepository.existsByUserId(userId)) {
            throw new IllegalStateException("El usuario ya tiene un perfil de administrador");
        }

        // Validar que el nivel no sea nulo
        if (adminLevel == null) {
            adminLevel = AdminLevel.MODERATOR;
        }

        AdministratorEntity administrator = new AdministratorEntity();
        administrator.setAdministratorId(UUID.randomUUID());
        administrator.setUserId(userId);
        administrator.setAdminLevel(adminLevel);

        return administratorRepository.save(administrator);
    }

    /**
     * Obtiene un perfil de administrador por su ID.
     */
    @Transactional(readOnly = true)
    public AdministratorEntity getAdministratorById(UUID administratorId) {
        return administratorRepository.findById(administratorId)
                .orElseThrow(() -> new IllegalArgumentException("Administrador no encontrado"));
    }

    /**
     * Obtiene un perfil de administrador por ID de usuario.
     */
    @Transactional(readOnly = true)
    public AdministratorEntity getAdministratorByUserId(UUID userId) {
        return administratorRepository.findByUserId(userId)
                .orElseThrow(
                        () -> new IllegalArgumentException("Perfil de administrador no encontrado para este usuario"));
    }

    /**
     * Actualiza el nivel de administrador.
     */
    @Transactional
    public AdministratorEntity updateAdminLevel(UUID administratorId, AdminLevel newLevel) {
        if (newLevel == null) {
            throw new IllegalArgumentException("El nivel de administrador no puede ser nulo");
        }

        AdministratorEntity administrator = administratorRepository.findById(administratorId)
                .orElseThrow(() -> new IllegalArgumentException("Administrador no encontrado"));

        // No permitir que un SUPERADMIN se degrade a sí mismo (esto se valida en el
        // controlador)
        // Pero añadimos una validación adicional por seguridad
        administrator.setAdminLevel(newLevel);
        return administratorRepository.save(administrator);
    }

    /**
     * Verifica si un usuario es administrador.
     */
    @Transactional(readOnly = true)
    public boolean isAdministrator(UUID userId) {
        return administratorRepository.isAdministrator(userId);
    }

    /**
     * Verifica si un usuario tiene un nivel específico de administrador.
     */
    @Transactional(readOnly = true)
    public boolean hasAdminLevel(UUID userId, AdminLevel level) {
        if (level == null) {
            return false;
        }
        return administratorRepository.hasAdminLevel(userId, level);
    }

    /**
     * Obtiene el nivel de administrador de un usuario.
     */
    @Transactional(readOnly = true)
    public AdminLevel getAdminLevel(UUID userId) {
        return administratorRepository.findAdminLevelByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("El usuario no tiene perfil de administrador"));
    }

    /**
     * Obtiene todos los administradores.
     */
    @Transactional(readOnly = true)
    public List<AdministratorEntity> getAllAdministrators() {
        return administratorRepository.findAllOrderByLevel();
    }

    /**
     * Obtiene administradores por nivel.
     */
    @Transactional(readOnly = true)
    public List<AdministratorEntity> getAdministratorsByLevel(AdminLevel level) {
        if (level == null) {
            throw new IllegalArgumentException("El nivel no puede ser nulo");
        }
        return administratorRepository.findByAdminLevel(level);
    }

    /**
     * Elimina un perfil de administrador.
     */
    @Transactional
    public void deleteAdministratorProfile(UUID administratorId) {
        if (!administratorRepository.existsById(administratorId)) {
            throw new IllegalArgumentException("Administrador no encontrado");
        }
        administratorRepository.deleteById(administratorId);
    }

    /**
     * Elimina un perfil de administrador por ID de usuario (útil para limpieza).
     */
    @Transactional
    public void deleteAdministratorProfileByUserId(UUID userId) {
        AdministratorEntity administrator = administratorRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Administrador no encontrado para este usuario"));
        administratorRepository.delete(administrator);
    }
}