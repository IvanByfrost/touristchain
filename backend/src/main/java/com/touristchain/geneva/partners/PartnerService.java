package com.touristchain.geneva.partners;

import com.touristchain.geneva.users.UserEntity;
import com.touristchain.geneva.users.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PartnerService {

    private final PartnerRepository partnerRepository;
    private final UserRepository userRepository;

    /**
     * Crea un perfil de partner para un usuario existente.
     */
    @Transactional
    public PartnerEntity createPartnerProfile(UUID userId, CreatePartnerRequest request) {
        // Verificar que el usuario existe
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        // Verificar que el usuario no tenga ya un perfil de partner
        if (partnerRepository.existsByUserId(userId)) {
            throw new IllegalStateException("El usuario ya tiene un perfil de partner");
        }

        // Verificar que el NIT no exista (si se proporcionó)
        if (request.taxId() != null && !request.taxId().isBlank()) {
            if (partnerRepository.existsByTaxId(request.taxId())) {
                throw new IllegalArgumentException("El NIT ya está registrado");
            }
        }

        PartnerEntity partner = new PartnerEntity();
        partner.setPartnerId(UUID.randomUUID());
        partner.setUserId(userId);
        partner.setCompanyName(request.companyName());
        partner.setTaxId(request.taxId());
        partner.setBusinessType(request.businessType());
        partner.setCompanyDescription(request.companyDescription());
        partner.setWebsite(request.website());
        partner.setContactEmail(request.contactEmail());
        partner.setContactPhone(request.contactPhone());
        partner.setCountryId(request.countryId());

        return partnerRepository.save(partner);
    }

    /**
     * Obtiene un partner por su ID.
     */
    @Transactional(readOnly = true)
    public PartnerEntity getPartnerById(UUID partnerId) {
        return partnerRepository.findById(partnerId)
                .orElseThrow(() -> new IllegalArgumentException("Partner no encontrado"));
    }

    /**
     * Obtiene un partner por ID de usuario.
     */
    @Transactional(readOnly = true)
    public PartnerEntity getPartnerByUserId(UUID userId) {
        return partnerRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Perfil de partner no encontrado para este usuario"));
    }

    /**
     * Actualiza el perfil de partner.
     */
    @Transactional
    public PartnerEntity updatePartnerProfile(UUID partnerId, UpdatePartnerRequest request) {
        PartnerEntity partner = partnerRepository.findById(partnerId)
                .orElseThrow(() -> new IllegalArgumentException("Partner no encontrado"));

        // Validar que el NIT no esté siendo usado por otro partner
        if (request.taxId() != null && !request.taxId().isBlank()) {
            if (!request.taxId().equals(partner.getTaxId())) {
                if (partnerRepository.existsByTaxId(request.taxId())) {
                    throw new IllegalArgumentException("El NIT ya está registrado por otro partner");
                }
                partner.setTaxId(request.taxId());
            }
        }

        if (request.companyName() != null && !request.companyName().isBlank()) {
            partner.setCompanyName(request.companyName());
        }
        if (request.businessType() != null && !request.businessType().isBlank()) {
            partner.setBusinessType(request.businessType());
        }
        if (request.companyDescription() != null) {
            partner.setCompanyDescription(request.companyDescription());
        }
        if (request.website() != null && !request.website().isBlank()) {
            partner.setWebsite(request.website());
        }
        if (request.contactEmail() != null && !request.contactEmail().isBlank()) {
            partner.setContactEmail(request.contactEmail());
        }
        if (request.contactPhone() != null && !request.contactPhone().isBlank()) {
            partner.setContactPhone(request.contactPhone());
        }
        if (request.countryId() != null) {
            partner.setCountryId(request.countryId());
        }

        return partnerRepository.save(partner);
    }

    /**
     * Obtiene todos los partners ordenados por nombre.
     */
    @Transactional(readOnly = true)
    public List<PartnerEntity> getAllPartners() {
        return partnerRepository.findAllOrderByCompanyName();
    }

    /**
     * Obtiene partners por tipo de negocio.
     */
    @Transactional(readOnly = true)
    public List<PartnerEntity> getPartnersByBusinessType(String businessType) {
        if (businessType == null || businessType.isBlank()) {
            throw new IllegalArgumentException("El tipo de negocio es obligatorio");
        }
        return partnerRepository.findByBusinessType(businessType);
    }

    /**
     * Obtiene partners por país.
     */
    @Transactional(readOnly = true)
    public List<PartnerEntity> getPartnersByCountry(UUID countryId) {
        if (countryId == null) {
            throw new IllegalArgumentException("El ID del país es obligatorio");
        }
        return partnerRepository.findByCountryId(countryId);
    }

    /**
     * Busca partners por nombre (contiene, ignorando mayúsculas).
     */
    @Transactional(readOnly = true)
    public List<PartnerEntity> searchPartnersByName(String companyName) {
        if (companyName == null || companyName.isBlank()) {
            throw new IllegalArgumentException("El nombre de la empresa es obligatorio");
        }
        return partnerRepository.findByCompanyNameContainingIgnoreCase(companyName);
    }

    /**
     * Obtiene los tipos de negocio disponibles.
     */
    @Transactional(readOnly = true)
    public List<String> getDistinctBusinessTypes() {
        return partnerRepository.findDistinctBusinessTypes();
    }

    /**
     * Obtiene partners creados antes de una fecha.
     */
    @Transactional(readOnly = true)
    public List<PartnerEntity> getPartnersCreatedBefore(LocalDateTime threshold) {
        if (threshold == null) {
            throw new IllegalArgumentException("La fecha límite es obligatoria");
        }
        return partnerRepository.findPartnersCreatedBefore(threshold);
    }

    /**
     * Cuenta partners por tipo de negocio.
     */
    @Transactional(readOnly = true)
    public Map<String, Long> countPartnersByBusinessType() {
        List<Object[]> results = partnerRepository.countPartnersByBusinessType();
        return results.stream()
                .collect(Collectors.toMap(
                        row -> (String) row[0],
                        row -> (Long) row[1]));
    }

    /**
     * Elimina un perfil de partner.
     */
    @Transactional
    public void deletePartnerProfile(UUID partnerId) {
        if (!partnerRepository.existsById(partnerId)) {
            throw new IllegalArgumentException("Partner no encontrado");
        }
        partnerRepository.deleteById(partnerId);
    }

    public record CreatePartnerRequest(
            String companyName,
            String taxId,
            String businessType,
            String companyDescription,
            String website,
            String contactEmail,
            String contactPhone,
            UUID countryId) {
    }

    public record UpdatePartnerRequest(
            String companyName,
            String taxId,
            String businessType,
            String companyDescription,
            String website,
            String contactEmail,
            String contactPhone,
            UUID countryId) {
    }
}