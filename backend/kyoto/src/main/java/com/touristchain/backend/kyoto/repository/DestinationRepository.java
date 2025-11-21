package com.touristchain.backend.kyoto.repository;

import com.touristchain.backend.kyoto.models.Destination;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DestinationRepository extends JpaRepository<Destination, Long> {
    
    // Buscar por país
    List<Destination> findByCountry(String country);
    
    // Buscar por ciudad
    List<Destination> findByCity(String city);
    
    // Buscar por categoría
    List<Destination> findByCategory(String category);
    
    // Buscar por nombre (que contenga)
    List<Destination> findByNameContainingIgnoreCase(String name);
    
    // Buscar por rango de precio
    List<Destination> findByPricePerDayBetween(Double minPrice, Double maxPrice);
    
    // Destinos populares (mejor rating primero)
    List<Destination> findByOrderByRatingDesc();
    
    // Destinos activos
    List<Destination> findByActiveTrue();
    
    // Búsqueda con filtros combinados
    @Query("SELECT d FROM Destination d WHERE " +
           "(:country IS NULL OR d.country = :country) AND " +
           "(:category IS NULL OR d.category = :category) AND " +
           "(:minPrice IS NULL OR d.pricePerDay >= :minPrice) AND " +
           "(:maxPrice IS NULL OR d.pricePerDay <= :maxPrice)")
    List<Destination> findWithFilters(
        @Param("country") String country,
        @Param("category") String category,
        @Param("minPrice") Double minPrice,
        @Param("maxPrice") Double maxPrice
    );
}