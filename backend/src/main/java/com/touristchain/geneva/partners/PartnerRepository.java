package com.touristchain.geneva.partners;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PartnerRepository extends JpaRepository<PartnerEntity, UUID> {

    /**
     * Busca un partner por el ID de usuario asociado.
     */
    Optional<PartnerEntity> findByUserId(UUID userId);

    /**
     * Verifica si existe un partner con un ID de usuario específico.
     */
    boolean existsByUserId(UUID userId);

    /**
     * Busca partners por tipo de negocio.
     */
    List<PartnerEntity> findByBusinessType(String businessType);

    /**
     * Busca partners por país.
     */
    List<PartnerEntity> findByCountryId(UUID countryId);

    /**
     * Busca partners por nombre de empresa (contiene, ignorando mayúsculas).
     */
    List<PartnerEntity> findByCompanyNameContainingIgnoreCase(String companyName);

    /**
     * Busca un partner por NIT.
     */
    Optional<PartnerEntity> findByTaxId(String taxId);

    /**
     * Verifica si existe un partner con un NIT específico.
     */
    boolean existsByTaxId(String taxId);

    /**
     * Obtiene todos los partners ordenados por nombre de empresa.
     */
    @Query("SELECT p FROM PartnerEntity p ORDER BY p.companyName ASC")
    List<PartnerEntity> findAllOrderByCompanyName();

    /**
     * Obtiene los tipos de negocio disponibles (distinct).
     */
    @Query("SELECT DISTINCT p.businessType FROM PartnerEntity p WHERE p.businessType IS NOT NULL")
    List<String> findDistinctBusinessTypes();

    /**
     * Obtiene partners con más de X años de antigüedad (usando created_at).
     */
    @Query("SELECT p FROM PartnerEntity p WHERE p.createdAt <= :threshold")
    List<PartnerEntity> findPartnersCreatedBefore(@Param("threshold") LocalDateTime threshold);

    /**
     * Cuenta partners por tipo de negocio.
     */
    @Query("SELECT p.businessType, COUNT(p) FROM PartnerEntity p GROUP BY p.businessType")
    List<Object[]> countPartnersByBusinessType();
}