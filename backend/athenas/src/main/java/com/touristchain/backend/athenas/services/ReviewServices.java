package main.java.com.touristchain.backend.athenas.services;

import com.touristchain.backend.athenas.models.ReviewModels.Review;
import com.touristchain.backend.athenas.models.ReviewModels.ReputationScore;
import com.touristchain.backend.athenas.models.ReviewModels.DestinationRating;
import com.touristchain.backend.athenas.repositories.ReviewRepository;
import com.touristchain.backend.athenas.repositories.ReputationScoreRepository;
import com.touristchain.backend.athenas.repositories.DestinationRatingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ReviewServices {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ReputationScoreRepository reputationScoreRepository;

    @Autowired
    private DestinationRatingRepository destinationRatingRepository;

    // Crear nueva reseña
    public Review createReview(Review review) {
        // Validar rating
        if (review.getRating() == null || review.getRating() < 1 || review.getRating() > 5) {
            throw new IllegalArgumentException("El rating debe estar entre 1 y 5");
        }

        // Validar que no exista reseña para este booking
        if (reviewRepository.existsByBookingId(review.getBookingId())) {
            throw new IllegalArgumentException("Ya existe una reseña para esta reserva");
        }

        // Validar que el turista no haya reseñado este destino antes
        if (reviewRepository.existsByDestinationIdAndTouristId(review.getDestinationId(), review.getTouristId())) {
            throw new IllegalArgumentException("Ya has reseñado este destino anteriormente");
        }

        review.setCreatedAt(LocalDateTime.now());
        review.setUpdatedAt(LocalDateTime.now());
        review.setVerified(false);

        Review savedReview = reviewRepository.save(review);

        // Actualizar reputaciones
        updateProviderReputation(review.getProviderId());
        updateDestinationRating(review.getDestinationId());

        return savedReview;
    }

    // Obtener reseña por ID
    public Optional<Review> findById(Long id) {
        return reviewRepository.findById(id);
    }

    // Obtener reseñas por destino
    public List<Review> findByDestinationId(Long destinationId) {
        return reviewRepository.findByDestinationId(destinationId);
    }

    // Obtener reseñas por proveedor
    public List<Review> findByProviderId(Long providerId) {
        return reviewRepository.findByProviderId(providerId);
    }

    // Obtener reseñas por turista
    public List<Review> findByTouristId(Long touristId) {
        return reviewRepository.findByTouristId(touristId);
    }

    // Obtener reseña por booking
    public Optional<Review> findByBookingId(Long bookingId) {
        return reviewRepository.findByBookingId(bookingId);
    }

    // Agregar respuesta del proveedor
    public Optional<Review> addProviderResponse(Long reviewId, String response) {
        Optional<Review> reviewOpt = reviewRepository.findById(reviewId);
        
        if (reviewOpt.isPresent()) {
            Review review = reviewOpt.get();
            
            if (review.getResponse() != null) {
                throw new IllegalStateException("Esta reseña ya tiene una respuesta");
            }

            review.setResponse(response);
            review.setRespondedAt(LocalDateTime.now());
            review.setUpdatedAt(LocalDateTime.now());

            // Actualizar tasa de respuesta del proveedor
            updateProviderResponseRate(review.getProviderId());

            return Optional.of(reviewRepository.save(review));
        }
        
        return Optional.empty();
    }

    // Verificar reseña en blockchain
    public Optional<Review> verifyReview(Long reviewId, String transactionHash) {
        Optional<Review> reviewOpt = reviewRepository.findById(reviewId);
        
        if (reviewOpt.isPresent()) {
            Review review = reviewOpt.get();
            
            if (review.getVerified()) {
                throw new IllegalStateException("La reseña ya está verificada");
            }

            review.setVerified(true);
            review.setTransactionHash(transactionHash);
            review.setUpdatedAt(LocalDateTime.now());

            return Optional.of(reviewRepository.save(review));
        }
        
        return Optional.empty();
    }

    // Obtener reputación de proveedor
    public Optional<ReputationScore> getProviderReputation(Long providerId) {
        return reputationScoreRepository.findByProviderId(providerId);
    }

    // Obtener rating de destino
    public Optional<DestinationRating> getDestinationRating(Long destinationId) {
        return destinationRatingRepository.findByDestinationId(destinationId);
    }

    // Obtener mejores proveedores
    public List<ReputationScore> getTopProviders(int limit) {
        List<ReputationScore> providers = reputationScoreRepository.findByOrderByOverallRatingDesc();
        return providers.stream().limit(limit).toList();
    }

    // Obtener mejores destinos
    public List<DestinationRating> getTopDestinations(int limit) {
        List<DestinationRating> destinations = destinationRatingRepository.findByOrderByOverallRatingDesc();
        return destinations.stream().limit(limit).toList();
    }

    // Obtener reseñas recientes de un destino
    public List<Review> getRecentDestinationReviews(Long destinationId, int limit) {
        List<Review> reviews = reviewRepository.findByDestinationIdOrderByCreatedAtDesc(destinationId);
        return reviews.stream().limit(limit).toList();
    }

    // Obtener reseñas recientes de un proveedor
    public List<Review> getRecentProviderReviews(Long providerId, int limit) {
        List<Review> reviews = reviewRepository.findByProviderIdOrderByCreatedAtDesc(providerId);
        return reviews.stream().limit(limit).toList();
    }

    // Calcular estadísticas de reseñas
    public Object getReviewStatistics() {
        List<Object[]> stats = reviewRepository.getReviewStatistics();
        
        if (!stats.isEmpty()) {
            Object[] data = stats.get(0);
            return new Object() {
                public final Long totalReviews = (Long) data[0];
                public final Double averageRating = (Double) data[1];
                public final Integer minRating = (Integer) data[2];
                public final Integer maxRating = (Integer) data[3];
            };
        }
        
        return new Object() {
            public final Long totalReviews = 0L;
            public final Double averageRating = 0.0;
            public final Integer minRating = 0;
            public final Integer maxRating = 0;
        };
    }

    // Obtener distribución de ratings por proveedor
    public List<Object[]> getProviderRatingDistribution(Long providerId) {
        return reviewRepository.getRatingDistributionByProviderId(providerId);
    }

    // Métodos privados para actualizar reputaciones
    private void updateProviderReputation(Long providerId) {
        List<Review> reviews = reviewRepository.findByProviderId(providerId);
        
        ReputationScore reputation = reputationScoreRepository.findByProviderId(providerId)
            .orElse(new ReputationScore(providerId));
        
        // Reiniciar contadores
        reputation.setFiveStar(0);
        reputation.setFourStar(0);
        reputation.setThreeStar(0);
        reputation.setTwoStar(0);
        reputation.setOneStar(0);
        
        // Contar ratings
        for (Review review : reviews) {
            reputation.incrementStarCount(review.getRating());
        }
        
        // Recalcular
        reputation.recalculate();
        reputation.setLastUpdated(LocalDateTime.now());
        
        // Calcular tasa de respuesta
        updateProviderResponseRate(providerId);
        
        reputationScoreRepository.save(reputation);
    }

    private void updateProviderResponseRate(Long providerId) {
        List<Review> reviews = reviewRepository.findByProviderId(providerId);
        
        if (!reviews.isEmpty()) {
            long withResponse = reviews.stream().filter(r -> r.getResponse() != null).count();
            double responseRate = (double) withResponse / reviews.size() * 100;
            
            ReputationScore reputation = reputationScoreRepository.findByProviderId(providerId)
                .orElse(new ReputationScore(providerId));
            
            reputation.setResponseRate(responseRate);
            reputationScoreRepository.save(reputation);
        }
    }

    private void updateDestinationRating(Long destinationId) {
        List<Review> reviews = reviewRepository.findByDestinationId(destinationId);
        
        DestinationRating rating = destinationRatingRepository.findByDestinationId(destinationId)
            .orElse(new DestinationRating(destinationId));
        
        if (!reviews.isEmpty()) {
            // Calcular promedios
            double totalRating = 0;
            double totalCleanliness = 0;
            double totalService = 0;
            double totalLocation = 0;
            double totalValue = 0;
            
            int cleanlinessCount = 0;
            int serviceCount = 0;
            int locationCount = 0;
            int valueCount = 0;
            
            for (Review review : reviews) {
                totalRating += review.getRating();
                
                if (review.getCleanliness() != null) {
                    totalCleanliness += review.getCleanliness();
                    cleanlinessCount++;
                }
                if (review.getService() != null) {
                    totalService += review.getService();
                    serviceCount++;
                }
                if (review.getLocation() != null) {
                    totalLocation += review.getLocation();
                    locationCount++;
                }
                if (review.getValue() != null) {
                    totalValue += review.getValue();
                    valueCount++;
                }
            }
            
            rating.setTotalReviews(reviews.size());
            rating.setOverallRating(totalRating / reviews.size());
            rating.setCleanliness(cleanlinessCount > 0 ? totalCleanliness / cleanlinessCount : null);
            rating.setService(serviceCount > 0 ? totalService / serviceCount : null);
            rating.setLocation(locationCount > 0 ? totalLocation / locationCount : null);
            rating.setValue(valueCount > 0 ? totalValue / valueCount : null);
            rating.setLastUpdated(LocalDateTime.now());
            
            destinationRatingRepository.save(rating);
        }
    }

    // Buscar reseñas de alta calidad
    public List<Review> findHighQualityReviews(Integer minCleanliness, Integer minService) {
        return reviewRepository.findHighQualityReviews(minCleanliness, minService);
    }

    // Buscar destinos de alta calidad
    public List<DestinationRating> findHighQualityDestinations(Double minCleanliness, Double minService) {
        return destinationRatingRepository.findHighQualityDestinations(minCleanliness, minService);
    }

    // Obtener promedio de ratings globales
    public Object getAverageRatings() {
        List<Object[]> averages = destinationRatingRepository.getAverageRatings();
        
        if (!averages.isEmpty()) {
            Object[] data = averages.get(0);
            return new Object() {
                public final Double overall = (Double) data[0];
                public final Double cleanliness = (Double) data[1];
                public final Double service = (Double) data[2];
                public final Double location = (Double) data[3];
                public final Double value = (Double) data[4];
            };
        }
        
        return new Object() {
            public final Double overall = 0.0;
            public final Double cleanliness = 0.0;
            public final Double service = 0.0;
            public final Double location = 0.0;
            public final Double value = 0.0;
        };
    }
}