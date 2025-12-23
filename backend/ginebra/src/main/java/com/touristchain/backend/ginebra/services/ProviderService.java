package com.touristchain.backend.ginebra.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.touristchain.backend.ginebra.dto.ProviderResponse;
import com.touristchain.backend.ginebra.models.entities.Provider;
import com.touristchain.backend.ginebra.models.entities.User;
import com.touristchain.backend.ginebra.repositories.ProviderRepository;
import com.touristchain.backend.ginebra.repositories.UserRepository;

@Service
@Transactional
public class ProviderService {

    @Autowired
    private ProviderRepository providerRepository;

    @Autowired
    private UserRepository userRepository;

    // ========== CONSULTAS ==========

    @Transactional(readOnly = true)
    public List<ProviderResponse> getAllProviders() {
        return providerRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<ProviderResponse> getProviderById(Long id) {
        return providerRepository.findById(id)
                .map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public Optional<ProviderResponse> getProviderByUserId(Long userId) {
        return providerRepository.findByUserId(userId)
                .map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public List<ProviderResponse> getVerifiedProviders() {
        return providerRepository.findByVerifiedTrue()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProviderResponse> getUnverifiedProviders() {
        return providerRepository.findByVerifiedFalse()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProviderResponse> searchProvidersByCompanyName(String name) {
        return providerRepository.findByCompanyNameContainingIgnoreCase(name)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ========== MUTACIONES ==========

    public ProviderResponse registerProvider(Provider provider) {
        // Validar que el usuario existe
        if (provider.getUser() == null || provider.getUser().getId() == null) {
            throw new RuntimeException("Usuario no válido");
        }

        User user = userRepository.findById(provider.getUser().getId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Validar que el usuario no sea ya proveedor
        if (providerRepository.existsByUserId(user.getId())) {
            throw new RuntimeException("El usuario ya es un proveedor registrado");
        }

        // Validar taxId único si se proporciona
        if (provider.getTaxId() != null &&
                providerRepository.findByTaxId(provider.getTaxId()).isPresent()) {
            throw new RuntimeException("Tax ID ya registrado");
        }

        // Asignar usuario y timestamps
        provider.setUser(user);
        provider.setCreatedAt(LocalDateTime.now());
        provider.setVerified(false);

        Provider savedProvider = providerRepository.save(provider);
        return toResponse(savedProvider);
    }

    public ProviderResponse verifyProvider(Long providerId) {
        Provider provider = providerRepository.findById(providerId)
                .orElseThrow(() -> new RuntimeException("Proveedor no encontrado"));

        provider.setVerified(true);
        provider.setVerifiedAt(LocalDateTime.now());

        Provider updatedProvider = providerRepository.save(provider);
        return toResponse(updatedProvider);
    }

    public ProviderResponse updateProvider(Long providerId, Provider providerUpdates) {
        Provider provider = providerRepository.findById(providerId)
                .orElseThrow(() -> new RuntimeException("Proveedor no encontrado"));

        // Actualizar campos permitidos
        if (providerUpdates.getCompanyName() != null) {
            provider.setCompanyName(providerUpdates.getCompanyName());
        }
        if (providerUpdates.getBusinessType() != null) {
            provider.setBusinessType(providerUpdates.getBusinessType());
        }
        if (providerUpdates.getTaxId() != null) {
            // Validar taxId único
            Optional<Provider> existing = providerRepository.findByTaxId(providerUpdates.getTaxId());
            if (existing.isPresent() && !existing.get().getId().equals(providerId)) {
                throw new RuntimeException("Tax ID ya registrado");
            }
            provider.setTaxId(providerUpdates.getTaxId());
        }
        if (providerUpdates.getLicenseNumber() != null) {
            provider.setLicenseNumber(providerUpdates.getLicenseNumber());
        }
        if (providerUpdates.getBusinessAddress() != null) {
            provider.setBusinessAddress(providerUpdates.getBusinessAddress());
        }

        return toResponse(providerRepository.save(provider));
    }

    public void addReview(Long providerId, Double rating) {
        Provider provider = providerRepository.findById(providerId)
                .orElseThrow(() -> new RuntimeException("Proveedor no encontrado"));

        provider.addReview(rating);
        providerRepository.save(provider);
    }

    public void deleteProvider(Long providerId) {
        if (!providerRepository.existsById(providerId)) {
            throw new RuntimeException("Proveedor no encontrado");
        }
        providerRepository.deleteById(providerId);
    }

    // ========== UTILIDADES ==========

    private ProviderResponse toResponse(Provider provider) {
        return new ProviderResponse(
                provider.getId(), // Long id
                provider.getUser().getId(), // Long userId
                provider.getCompanyName(), // String companyName
                provider.getBusinessType(), // BusinessType businessType
                provider.getTaxId(), // String taxId
                provider.getLicenseNumber(), // String licenseNumber
                provider.getBusinessAddress(), // String businessAddress
                provider.getVerified(), // Boolean verified
                provider.getOverallRating(), // Double overallRating
                provider.getTotalReviews(), // Integer totalReviews
                provider.getCreatedAt(), // LocalDateTime createdAt
                provider.getVerifiedAt() // LocalDateTime verifiedAt
        );
    }

    @Transactional(readOnly = true)
    public boolean existsByUserId(Long userId) {
        return providerRepository.existsByUserId(userId);
    }

    @Transactional(readOnly = true)
    public long countProviders() {
        return providerRepository.count();
    }

    @Transactional(readOnly = true)
    public long countVerifiedProviders() {
        return providerRepository.findByVerifiedTrue().size();
    }
}