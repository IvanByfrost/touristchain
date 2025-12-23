package com.touristchain.backend.ginebra.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.touristchain.backend.ginebra.models.entities.Provider;
import com.touristchain.backend.ginebra.models.entities.User;

@Repository
public interface ProviderRepository extends JpaRepository<Provider, Long> {

    // Buscar proveedor por usuario
    Optional<Provider> findByUser(User user);

    // Buscar proveedor por ID de usuario
    Optional<Provider> findByUserId(Long userId);

    // Buscar proveedores verificados
    List<Provider> findByVerifiedTrue();

    // Buscar proveedores no verificados
    List<Provider> findByVerifiedFalse();

    // Buscar proveedores por tipo de negocio
    List<Provider> findByBusinessType(String businessType);

    // Verificar si existe proveedor para un usuario
    boolean existsByUserId(Long userId);

    // Buscar por nombre de empresa (insensible a mayúsculas)
    List<Provider> findByCompanyNameContainingIgnoreCase(String companyName);

    // Buscar por taxId
    Optional<Provider> findByTaxId(String taxId);
}