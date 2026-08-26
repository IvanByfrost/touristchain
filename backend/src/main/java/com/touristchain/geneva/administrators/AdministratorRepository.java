package com.touristchain.geneva.administrators;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.touristchain.geneva.administrators.AdministratorEntity.AdminLevel;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AdministratorRepository extends JpaRepository<AdministratorEntity, UUID> {

    /**
     * Busca un administrador por el ID de usuario asociado.
     */
    Optional<AdministratorEntity> findByUserId(UUID userId);

    /**
     * Verifica si existe un administrador con un ID de usuario específico.
     */
    boolean existsByUserId(UUID userId);

    /**
     * Busca administradores por nivel.
     */
    List<AdministratorEntity> findByAdminLevel(AdminLevel adminLevel);

    /**
     * Obtiene todos los administradores ordenados por nivel.
     */
    @Query("SELECT a FROM AdministratorEntity a ORDER BY a.adminLevel ASC")
    List<AdministratorEntity> findAllOrderByLevel();

    /**
     * Verifica si un usuario es administrador.
     */
    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END FROM AdministratorEntity a WHERE a.userId = :userId")
    boolean isAdministrator(@Param("userId") UUID userId);

    /**
     * Verifica si un usuario tiene un nivel específico de administrador.
     */
    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END FROM AdministratorEntity a WHERE a.userId = :userId AND a.adminLevel = :level")
    boolean hasAdminLevel(@Param("userId") UUID userId, @Param("level") AdminLevel level);

    /**
     * Obtiene el nivel de administrador de un usuario.
     */
    @Query("SELECT a.adminLevel FROM AdministratorEntity a WHERE a.userId = :userId")
    Optional<AdminLevel> findAdminLevelByUserId(@Param("userId") UUID userId);
}