package com.touristchain.backend.kyoto.repositories;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.touristchain.backend.kyoto.models.ServiceType;
import com.touristchain.backend.kyoto.models.entities.TouristService;

@Repository
public interface TouristServiceRepository extends JpaRepository<TouristService, Long> {

    // ========== BUSQUEDAS POR PROVEEDOR ==========

    List<TouristService> findByProviderId(Long providerId);

    Page<TouristService> findByProviderId(Long providerId, Pageable pageable);

    // ========== FILTROS BÁSICOS ==========

    List<TouristService> findByServiceType(ServiceType serviceType);

    List<TouristService> findByLocation(String location);

    List<TouristService> findByAvailableTrue();

    List<TouristService> findByAvailableFalse();

    // ========== FILTROS COMBINADOS ==========

    List<TouristService> findByServiceTypeAndLocation(ServiceType serviceType, String location);

    List<TouristService> findByServiceTypeAndAvailableTrue(ServiceType serviceType);

    List<TouristService> findByLocationAndAvailableTrue(String location);

    // ========== BUSQUEDA POR PRECIO ==========

    List<TouristService> findByPriceLessThanEqual(BigDecimal maxPrice);

    List<TouristService> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);

    List<TouristService> findByServiceTypeAndPriceBetween(ServiceType serviceType, BigDecimal minPrice,
            BigDecimal maxPrice);

    List<TouristService> findByLocationAndPriceBetween(String location, BigDecimal minPrice, BigDecimal maxPrice);

    // ========== BUSQUEDA POR RATING ==========

    List<TouristService> findByRatingGreaterThanEqual(BigDecimal minRating);

    // ========== BUSQUEDA POR TAGS ==========

    @Query("SELECT ts FROM TouristService ts JOIN ts.tags t WHERE t = :tag")
    List<TouristService> findByTag(@Param("tag") String tag);

    @Query("SELECT ts FROM TouristService ts JOIN ts.tags t WHERE t IN :tags")
    List<TouristService> findByTagsIn(@Param("tags") List<String> tags);

    // ========== BUSQUEDA TEXTO (nombre/descripción) ==========

    @Query("SELECT ts FROM TouristService ts WHERE " +
            "LOWER(ts.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(ts.description) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<TouristService> searchByText(@Param("query") String query);

    @Query("SELECT ts FROM TouristService ts WHERE " +
            "LOWER(ts.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(ts.description) LIKE LOWER(CONCAT('%', :query, '%')) AND " +
            "ts.available = true")
    List<TouristService> searchAvailableByText(@Param("query") String query);

    // ========== BUSQUEDA AVANZADA (múltiples filtros) ==========

    @Query("SELECT ts FROM TouristService ts WHERE " +
            "(:type IS NULL OR ts.serviceType = :type) AND " +
            "(:location IS NULL OR ts.location = :location) AND " +
            "(:minPrice IS NULL OR ts.price >= :minPrice) AND " +
            "(:maxPrice IS NULL OR ts.price <= :maxPrice) AND " +
            "ts.available = true")
    List<TouristService> searchWithFilters(
            @Param("type") ServiceType type,
            @Param("location") String location,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice);

    // ========== ESTADÍSTICAS ==========

    @Query("SELECT COUNT(ts) FROM TouristService ts WHERE ts.providerId = :providerId")
    long countByProviderId(@Param("providerId") Long providerId);

    @Query("SELECT ts.serviceType, COUNT(ts) FROM TouristService ts GROUP BY ts.serviceType")
    List<Object[]> countByServiceType();

    @Query("SELECT ts.location, COUNT(ts) FROM TouristService ts GROUP BY ts.location")
    List<Object[]> countByLocation();

    // ========== ORDENAMIENTO ==========

    List<TouristService> findByAvailableTrueOrderByPriceAsc();

    List<TouristService> findByAvailableTrueOrderByPriceDesc();

    List<TouristService> findByAvailableTrueOrderByRatingDesc();

    List<TouristService> findByAvailableTrueOrderByCreatedAtDesc();

    // ========== PAGINACIÓN ==========

    Page<TouristService> findAll(Pageable pageable);

    Page<TouristService> findByAvailableTrue(Pageable pageable);

    Page<TouristService> findByServiceType(ServiceType serviceType, Pageable pageable);

    Page<TouristService> findByLocation(String location, Pageable pageable);
}