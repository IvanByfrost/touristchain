package main.java.com.touristchain.backend.athenas.repositories;

import com.touristchain.backend.athenas.models.ReviewModels.Review;
import com.touristchain.backend.athenas.models.ReviewModels.ReputationScore;
import com.touristchain.backend.athenas.models.ReviewModels.DestinationRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    
    // Buscar reseñas por destino
    List<Review> findByDestinationId(Long destinationId);
    
    // Buscar reseñas por proveedor
    List<Review> findByProviderId(Long providerId);
    
    // Buscar reseñas por turista
    List<Review> findByTouristId(Long touristId);
    
    // Buscar reseñas por booking
    Optional<Review> findByBookingId(Long bookingId);
    
    // Buscar reseñas por rating
    List<Review> findByRating(Integer rating);
    
    // Buscar reseñas verificadas
    List<Review> findByVerifiedTrue();
    
    // Buscar reseñas con respuesta
    List<Review> findByResponseIsNotNull();
    
    // Buscar reseñas sin respuesta
    List<Review> findByResponseIsNull();
    
    // Buscar reseñas por rango de rating
    List<Review> findByRatingBetween(Integer minRating, Integer maxRating);
    
    // Verificar si existe reseña para un booking
    boolean existsByBookingId(Long bookingId);
    
    // Verificar si existe reseña para destino y turista
    boolean existsByDestinationIdAndTouristId(Long destinationId, Long touristId);
    
    // Contar reseñas por proveedor
    @Query("SELECT COUNT(r) FROM Review r WHERE r.providerId = :providerId")
    Long countByProviderId(@Param("providerId") Long providerId);
    
    // Calcular promedio de rating por proveedor
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.providerId = :providerId")
    Double getAverageRatingByProviderId(@Param("providerId") Long providerId);
    
    // Obtener distribución de ratings por proveedor
    @Query("SELECT r.rating, COUNT(r) FROM Review r WHERE r.providerId = :providerId GROUP BY r.rating ORDER BY r.rating DESC")
    List<Object[]> getRatingDistributionByProviderId(@Param("providerId") Long providerId);
    
    // Calcular promedio por destino
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.destinationId = :destinationId")
    Double getAverageRatingByDestinationId(@Param("destinationId") Long destinationId);
    
    // Obtener reseñas recientes de un destino
    List<Review> findByDestinationIdOrderByCreatedAtDesc(Long destinationId);
    
    // Obtener reseñas recientes de un proveedor
    List<Review> findByProviderIdOrderByCreatedAtDesc(Long providerId);
    
    // Buscar reseñas por múltiples destinos
    List<Review> findByDestinationIdIn(List<Long> destinationIds);
    
    // Buscar reseñas con calificaciones específicas
    @Query("SELECT r FROM Review r WHERE r.cleanliness >= :minCleanliness AND r.service >= :minService")
    List<Review> findHighQualityReviews(@Param("minCleanliness") Integer minCleanliness, 
                                       @Param("minService") Integer minService);
    
    // Obtener estadísticas de reseñas
    @Query("SELECT COUNT(r), AVG(r.rating), MIN(r.rating), MAX(r.rating) FROM Review r")
    List<Object[]> getReviewStatistics();
    
    // Buscar reseñas por transacción hash
    Optional<Review> findByTransactionHash(String transactionHash);
}

@Repository
public interface ReputationScoreRepository extends JpaRepository<ReputationScore, Long> {
    
    // Buscar reputación por proveedor
    Optional<ReputationScore> findByProviderId(Long providerId);
    
    // Buscar proveedores con mejor rating
    List<ReputationScore> findByOrderByOverallRatingDesc();
    
    // Buscar proveedores con más reseñas
    List<ReputationScore> findByOrderByTotalReviewsDesc();
    
    // Buscar proveedores con alta tasa de respuesta
    @Query("SELECT rs FROM ReputationScore rs WHERE rs.responseRate >= :minRate")
    List<ReputationScore> findByHighResponseRate(@Param("minRate") Double minRate);
    
    // Buscar proveedores verificados con buen rating
    @Query("SELECT rs FROM ReputationScore rs WHERE rs.overallRating >= :minRating")
    List<ReputationScore> findByMinimumRating(@Param("minRating") Double minRating);
}

@Repository
public interface DestinationRatingRepository extends JpaRepository<DestinationRating, Long> {
    
    // Buscar rating por destino
    Optional<DestinationRating> findByDestinationId(Long destinationId);
    
    // Buscar destinos mejor calificados
    List<DestinationRating> findByOrderByOverallRatingDesc();
    
    // Buscar destinos con más reseñas
    List<DestinationRating> findByOrderByTotalReviewsDesc();
    
    // Buscar destinos por categoría específica
    @Query("SELECT dr FROM DestinationRating dr WHERE dr.cleanliness >= :minCleanliness AND dr.service >= :minService")
    List<DestinationRating> findHighQualityDestinations(@Param("minCleanliness") Double minCleanliness, 
                                                       @Param("minService") Double minService);
    
    // Buscar destinos con buen valor
    @Query("SELECT dr FROM DestinationRating dr WHERE dr.value >= :minValue")
    List<DestinationRating> findByGoodValue(@Param("minValue") Double minValue);
    
    // Obtener estadísticas de ratings
    @Query("SELECT AVG(dr.overallRating), AVG(dr.cleanliness), AVG(dr.service), AVG(dr.location), AVG(dr.value) FROM DestinationRating dr")
    List<Object[]> getAverageRatings();
}