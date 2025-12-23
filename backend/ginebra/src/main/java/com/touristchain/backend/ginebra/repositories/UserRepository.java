package com.touristchain.backend.ginebra.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.touristchain.backend.ginebra.models.entities.User;
import com.touristchain.backend.ginebra.models.entities.UserType;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // ========== BUSQUEDAS ÚNICAS ==========
    Optional<User> findByEmail(String email);

    Optional<User> findByWalletAddress(String walletAddress);

    Optional<User> findByUsername(String username);

    // ========== EXISTENCIAS ==========
    boolean existsByEmail(String email);

    boolean existsByWalletAddress(String walletAddress);

    boolean existsByUsername(String username);

    // ========== FILTROS BÁSICOS ==========
    List<User> findByUserType(UserType userType);

    List<User> findByVerifiedTrue();

    List<User> findByActiveTrue();

    List<User> findByActiveFalse();

    // ========== FILTROS COMBINADOS ==========
    List<User> findByUserTypeAndActiveTrue(UserType userType);

    List<User> findByUserTypeAndVerifiedTrue(UserType userType);

    // ========== CON PAGINACIÓN ==========
    Page<User> findAll(Pageable pageable);

    Page<User> findByActiveTrue(Pageable pageable);

    Page<User> findByUserType(UserType userType, Pageable pageable);

    // ========== ESTADÍSTICAS ==========
    @Query("SELECT u.userType, COUNT(u) FROM User u GROUP BY u.userType")
    List<Object[]> countUsersByType();

    // ========== PARA PROVEEDORES ==========
    default List<User> findVerifiedProviders() {
        return findByUserTypeAndVerifiedTrue(UserType.PROVIDER);
    }
}