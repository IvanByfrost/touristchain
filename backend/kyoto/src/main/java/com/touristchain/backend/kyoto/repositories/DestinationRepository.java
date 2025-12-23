package com.touristchain.backend.kyoto.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.touristchain.backend.kyoto.models.entities.Destination;

@Repository
public interface DestinationRepository extends JpaRepository<Destination, Long> {

    // ========== BUSQUEDAS BÁSICAS ==========

    List<Destination> findByCountry(String country);

    Page<Destination> findByCountry(String country, Pageable pageable);

    List<Destination> findByCity(String city);

    Page<Destination> findByCity(String city, Pageable pageable);

    List<Destination> findByCategory(String category);

    Page<Destination> findByCategory(String category, Pageable pageable);

    // ========== BUSQUEDAS POR TEXTO ==========

    List<Destination> findByNameContainingIgnoreCase(String name);

    Page<Destination> findByNameContainingIgnoreCase(String name, Pageable pageable);

    @Query("SELECT d FROM Destination d WHERE " +
            "LOWER(d.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(d.description) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "EXISTS (SELECT t FROM d.tags t WHERE LOWER(t) LIKE LOWER(CONCAT('%', :query, '%')))")
    List<Destination> fullTextSearch(@Param("query") String query);

    @Query("SELECT d FROM Destination d WHERE " +
            "(LOWER(d.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(d.description) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "EXISTS (SELECT t FROM d.tags t WHERE LOWER(t) LIKE LOWER(CONCAT('%', :query, '%')))) AND " +
            "d.active = true")
    Page<Destination> fullTextSearchActive(@Param("query") String query, Pageable pageable);

    // ========== FILTROS POR PRECIO ==========

    List<Destination> findByPricePerDayBetween(Double minPrice, Double maxPrice);

    Page<Destination> findByPricePerDayBetween(Double minPrice, Double maxPrice, Pageable pageable);

    List<Destination> findByPricePerDayLessThanEqual(Double maxPrice);

    List<Destination> findByPricePerDayGreaterThanEqual(Double minPrice);

    // ========== ORDENAMIENTO ==========

    List<Destination> findByActiveTrueOrderByRatingDesc();

    Page<Destination> findByActiveTrueOrderByRatingDesc(Pageable pageable);

    List<Destination> findByActiveTrueOrderByPricePerDayAsc();

    List<Destination> findByActiveTrueOrderByPricePerDayDesc();

    List<Destination> findByActiveTrueOrderByCreatedAtDesc();

    Page<Destination> findByActiveTrueOrderByCreatedAtDesc(Pageable pageable);

    // ========== FILTROS COMBINADOS ==========

    @Query("SELECT d FROM Destination d WHERE " +
            "(:country IS NULL OR LOWER(d.country) = LOWER(:country)) AND " +
            "(:city IS NULL OR LOWER(d.city) = LOWER(:city)) AND " +
            "(:category IS NULL OR LOWER(d.category) = LOWER(:category)) AND " +
            "(:minPrice IS NULL OR d.pricePerDay >= :minPrice) AND " +
            "(:maxPrice IS NULL OR d.pricePerDay <= :maxPrice) AND " +
            "(:minRating IS NULL OR d.rating >= :minRating) AND " +
            "d.active = true")
    Page<Destination> findWithFilters(
            @Param("country") String country,
            @Param("city") String city,
            @Param("category") String category,
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice,
            @Param("minRating") Double minRating,
            Pageable pageable);

    // ========== DESTINOS POPULARES ==========

    @Query("SELECT d FROM Destination d WHERE " +
            "d.active = true AND " +
            "d.totalReviews >= :minReviews AND " +
            "d.rating >= :minRating " +
            "ORDER BY d.rating DESC, d.totalReviews DESC")
    Page<Destination> findPopular(
            @Param("minReviews") Integer minReviews,
            @Param("minRating") Double minRating,
            Pageable pageable);

    // ========== ESTADÍSTICAS ==========

    @Query("SELECT d.country, COUNT(d) FROM Destination d WHERE d.active = true GROUP BY d.country")
    List<Object[]> countByCountry();

    @Query("SELECT d.category, COUNT(d) FROM Destination d WHERE d.active = true GROUP BY d.category")
    List<Object[]> countByCategory();

    long countByActiveTrue();

    long countByActiveFalse();

    // ========== BUSQUEDAS ESPECÍFICAS ==========

    Optional<Destination> findByContractAddress(String contractAddress);

    List<Destination> findByProviderWallet(String providerWallet);

    @Query("SELECT d FROM Destination d WHERE " +
            "d.providerWallet = :wallet AND " +
            "d.active = true")
    Page<Destination> findByProviderWalletActive(
            @Param("wallet") String wallet,
            Pageable pageable);
}