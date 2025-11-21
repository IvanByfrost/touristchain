package com.touristchain.backend.ginebra.repositories;

import com.touristchain.backend.ginebra.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // Buscar por wallet address
    Optional<User> findByWalletAddress(String walletAddress);
    
    // Buscar por email
    Optional<User> findByEmail(String email);
    
    // Buscar por username
    Optional<User> findByUsername(String username);
    
    // Buscar usuarios por tipo
    List<User> findByUserType(String userType);
    
    // Buscar usuarios verificados
    List<User> findByVerifiedTrue();
    
    // Buscar usuarios activos
    List<User> findByActiveTrue();
    
    // Buscar por wallet address ignorando mayúsculas/minúsculas
    Optional<User> findByWalletAddressIgnoreCase(String walletAddress);
    
    // Verificar si existe un wallet address
    boolean existsByWalletAddress(String walletAddress);
    
    // Verificar si existe un email
    boolean existsByEmail(String email);
    
    // Verificar si existe un username
    boolean existsByUsername(String username);
    
    // Buscar usuarios por rango de fechas
    List<User> findByCreatedAtBetween(String startDate, String endDate);
    
    // Contar usuarios por tipo
    @Query("SELECT u.userType, COUNT(u) FROM User u GROUP BY u.userType")
    List<Object[]> countUsersByType();
    
    // Buscar usuarios por múltiples wallets
    List<User> findByWalletAddressIn(List<String> walletAddresses);
    
    // Buscar proveedores verificados
    @Query("SELECT u FROM User u WHERE u.userType = 'PROVIDER' AND u.verified = true")
    List<User> findVerifiedProviders();
    
    // Buscar por wallet address o email
    @Query("SELECT u FROM User u WHERE u.walletAddress = :identifier OR u.email = :identifier")
    Optional<User> findByWalletAddressOrEmail(@Param("identifier") String identifier);
}