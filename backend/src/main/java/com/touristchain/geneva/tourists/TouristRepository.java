package com.touristchain.geneva.tourists;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TouristRepository extends JpaRepository<TouristEntity, UUID> {

    /**
     * Busca un turista por el ID de usuario asociado.
     */
    Optional<TouristEntity> findByUserId(UUID userId);

    /**
     * Verifica si existe un turista con un ID de usuario específico.
     */
    boolean existsByUserId(UUID userId);

    /**
     * Busca turistas con puntos de fidelidad mayores o iguales a un valor.
     */
    List<TouristEntity> findByLoyaltyPointsGreaterThanEqual(Integer points);

    /**
     * Busca turistas que tengan una suscripción activa a boletines.
     */
    List<TouristEntity> findByNewsletterSubscriptionTrue();

    /**
     * Busca turistas por idioma preferido.
     */
    List<TouristEntity> findByPreferredLanguage(String language);

    /**
     * Query nativa: obtiene turistas con contacto de emergencia registrado.
     */
    @Query("SELECT t FROM TouristEntity t WHERE t.emergencyContactName IS NOT NULL AND t.emergencyContactPhone IS NOT NULL")
    List<TouristEntity> findWithEmergencyContact();

    /**
     * Query nativa: obtiene el total de puntos acumulados por un turista.
     */
    @Query("SELECT t.loyaltyPoints FROM TouristEntity t WHERE t.touristId = :touristId")
    Optional<Integer> findLoyaltyPointsByTouristId(@Param("touristId") UUID touristId);
}