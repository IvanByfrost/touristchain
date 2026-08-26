package com.touristchain.geneva.tourists;

import com.touristchain.geneva.users.UserEntity;
import com.touristchain.geneva.users.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TouristService {

    private final TouristRepository touristRepository;
    private final UserRepository userRepository;

    /**
     * Crea un perfil de turista para un usuario existente.
     */
    @Transactional
    public TouristEntity createTouristProfile(UUID userId, CreateTouristRequest request) {
        // Verificar que el usuario existe
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        // Verificar que el usuario no tenga ya un perfil de turista
        if (touristRepository.existsByUserId(userId)) {
            throw new IllegalStateException("El usuario ya tiene un perfil de turista");
        }

        TouristEntity tourist = new TouristEntity();
        tourist.setTouristId(UUID.randomUUID());
        tourist.setUserId(userId);
        tourist.setPreferredLanguage(request.preferredLanguage() != null ? request.preferredLanguage() : "es");
        tourist.setLoyaltyPoints(0);
        tourist.setTravelPreferences(request.travelPreferences());
        tourist.setEmergencyContactName(request.emergencyContactName());
        tourist.setEmergencyContactPhone(request.emergencyContactPhone());
        tourist.setNewsletterSubscription(
                request.newsletterSubscription() != null ? request.newsletterSubscription() : true);

        return touristRepository.save(tourist);
    }

    /**
     * Obtiene un perfil de turista por su ID.
     */
    @Transactional(readOnly = true)
    public TouristEntity getTouristById(UUID touristId) {
        return touristRepository.findById(touristId)
                .orElseThrow(() -> new IllegalArgumentException("Turista no encontrado"));
    }

    /**
     * Obtiene un perfil de turista por ID de usuario.
     */
    @Transactional(readOnly = true)
    public TouristEntity getTouristByUserId(UUID userId) {
        return touristRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Perfil de turista no encontrado para este usuario"));
    }

    /**
     * Actualiza el perfil de turista.
     */
    @Transactional
    public TouristEntity updateTouristProfile(UUID touristId, UpdateTouristRequest request) {
        TouristEntity tourist = touristRepository.findById(touristId)
                .orElseThrow(() -> new IllegalArgumentException("Turista no encontrado"));

        if (request.preferredLanguage() != null) {
            tourist.setPreferredLanguage(request.preferredLanguage());
        }
        if (request.travelPreferences() != null) {
            tourist.setTravelPreferences(request.travelPreferences());
        }
        if (request.emergencyContactName() != null) {
            tourist.setEmergencyContactName(request.emergencyContactName());
        }
        if (request.emergencyContactPhone() != null) {
            tourist.setEmergencyContactPhone(request.emergencyContactPhone());
        }
        if (request.newsletterSubscription() != null) {
            tourist.setNewsletterSubscription(request.newsletterSubscription());
        }

        return touristRepository.save(tourist);
    }

    /**
     * Agrega puntos de fidelidad a un turista.
     */
    @Transactional
    public TouristEntity addLoyaltyPoints(UUID touristId, Integer pointsToAdd) {
        if (pointsToAdd <= 0) {
            throw new IllegalArgumentException("Los puntos a agregar deben ser positivos");
        }

        TouristEntity tourist = touristRepository.findById(touristId)
                .orElseThrow(() -> new IllegalArgumentException("Turista no encontrado"));

        tourist.setLoyaltyPoints(tourist.getLoyaltyPoints() + pointsToAdd);
        return touristRepository.save(tourist);
    }

    /**
     * Obtiene todos los turistas suscritos a newsletters.
     */
    @Transactional(readOnly = true)
    public List<TouristEntity> getSubscribedToNewsletter() {
        return touristRepository.findByNewsletterSubscriptionTrue();
    }

    /**
     * Obtiene los puntos de fidelidad de un turista.
     */
    @Transactional(readOnly = true)
    public Integer getLoyaltyPoints(UUID touristId) {
        return touristRepository.findLoyaltyPointsByTouristId(touristId)
                .orElseThrow(() -> new IllegalArgumentException("Turista no encontrado"));
    }

    /**
     * Elimina el perfil de turista (desvincula al usuario).
     */
    @Transactional
    public void deleteTouristProfile(UUID touristId) {
        if (!touristRepository.existsById(touristId)) {
            throw new IllegalArgumentException("Turista no encontrado");
        }
        touristRepository.deleteById(touristId);
    }

    public record CreateTouristRequest(
            UUID userId, // <-- Añadido
            String preferredLanguage,
            String travelPreferences,
            String emergencyContactName,
            String emergencyContactPhone,
            Boolean newsletterSubscription) {
    }

    public record UpdateTouristRequest(
            String preferredLanguage,
            String travelPreferences,
            String emergencyContactName,
            String emergencyContactPhone,
            Boolean newsletterSubscription) {
    }

    public record AddLoyaltyPointsRequest(
            Integer pointsToAdd) {
    }
}