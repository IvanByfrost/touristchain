package main.java.com.touristchain.backend.athenas.controllers;

import com.touristchain.backend.athenas.models.ReviewModels.Review;
import com.touristchain.backend.athenas.models.ReviewModels.ReputationScore;
import com.touristchain.backend.athenas.models.ReviewModels.DestinationRating;
import com.touristchain.backend.athenas.services.ReviewServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/athenas/reviews")
@CrossOrigin(origins = "*")
public class ReviewController {

    @Autowired
    private ReviewServices reviewServices;

    // Crear nueva reseña
    @PostMapping
    public ResponseEntity<?> createReview(@RequestBody Review review) {
        try {
            Review newReview = reviewServices.createReview(review);
            return ResponseEntity.ok(newReview);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Obtener todas las reseñas
    @GetMapping
    public ResponseEntity<List<Review>> getAllReviews() {
        List<Review> reviews = reviewServices.findByDestinationId(null);
        return ResponseEntity.ok(reviews);
    }

    // Obtener reseña por ID
    @GetMapping("/{id}")
    public ResponseEntity<Review> getReviewById(@PathVariable Long id) {
        Optional<Review> review = reviewServices.findById(id);
        return review.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
    }

    // Obtener reseñas por destino
    @GetMapping("/destination/{destinationId}")
    public ResponseEntity<List<Review>> getReviewsByDestination(@PathVariable Long destinationId) {
        List<Review> reviews = reviewServices.findByDestinationId(destinationId);
        return ResponseEntity.ok(reviews);
    }

    // Obtener reseñas por proveedor
    @GetMapping("/provider/{providerId}")
    public ResponseEntity<List<Review>> getReviewsByProvider(@PathVariable Long providerId) {
        List<Review> reviews = reviewServices.findByProviderId(providerId);
        return ResponseEntity.ok(reviews);
    }

    // Obtener reseñas por turista
    @GetMapping("/tourist/{touristId}")
    public ResponseEntity<List<Review>> getReviewsByTourist(@PathVariable Long touristId) {
        List<Review> reviews = reviewServices.findByTouristId(touristId);
        return ResponseEntity.ok(reviews);
    }

    // Obtener reseña por booking
    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<Review> getReviewByBooking(@PathVariable Long bookingId) {
        Optional<Review> review = reviewServices.findByBookingId(bookingId);
        return review.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
    }

    // Agregar respuesta del proveedor
    @PatchMapping("/{id}/response")
    public ResponseEntity<?> addProviderResponse(@PathVariable Long id, @RequestBody ResponseRequest request) {
        try {
            Optional<Review> review = reviewServices.addProviderResponse(id, request.getResponse());
            return review.map(ResponseEntity::ok)
                        .orElse(ResponseEntity.notFound().build());
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Verificar reseña en blockchain
    @PatchMapping("/{id}/verify")
    public ResponseEntity<?> verifyReview(@PathVariable Long id, @RequestBody VerifyRequest request) {
        try {
            Optional<Review> review = reviewServices.verifyReview(id, request.getTransactionHash());
            return review.map(ResponseEntity::ok)
                        .orElse(ResponseEntity.notFound().build());
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Obtener reputación de proveedor
    @GetMapping("/provider/{providerId}/reputation")
    public ResponseEntity<ReputationScore> getProviderReputation(@PathVariable Long providerId) {
        Optional<ReputationScore> reputation = reviewServices.getProviderReputation(providerId);
        return reputation.map(ResponseEntity::ok)
                        .orElse(ResponseEntity.notFound().build());
    }

    // Obtener rating de destino
    @GetMapping("/destination/{destinationId}/rating")
    public ResponseEntity<DestinationRating> getDestinationRating(@PathVariable Long destinationId) {
        Optional<DestinationRating> rating = reviewServices.getDestinationRating(destinationId);
        return rating.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
    }

    // Obtener mejores proveedores
    @GetMapping("/providers/top")
    public ResponseEntity<List<ReputationScore>> getTopProviders(@RequestParam(defaultValue = "10") int limit) {
        List<ReputationScore> providers = reviewServices.getTopProviders(limit);
        return ResponseEntity.ok(providers);
    }

    // Obtener mejores destinos
    @GetMapping("/destinations/top")
    public ResponseEntity<List<DestinationRating>> getTopDestinations(@RequestParam(defaultValue = "10") int limit) {
        List<DestinationRating> destinations = reviewServices.getTopDestinations(limit);
        return ResponseEntity.ok(destinations);
    }

    // Obtener reseñas recientes de un destino
    @GetMapping("/destination/{destinationId}/recent")
    public ResponseEntity<List<Review>> getRecentDestinationReviews(
            @PathVariable Long destinationId,
            @RequestParam(defaultValue = "5") int limit) {
        List<Review> reviews = reviewServices.getRecentDestinationReviews(destinationId, limit);
        return ResponseEntity.ok(reviews);
    }

    // Obtener reseñas recientes de un proveedor
    @GetMapping("/provider/{providerId}/recent")
    public ResponseEntity<List<Review>> getRecentProviderReviews(
            @PathVariable Long providerId,
            @RequestParam(defaultValue = "5") int limit) {
        List<Review> reviews = reviewServices.getRecentProviderReviews(providerId, limit);
        return ResponseEntity.ok(reviews);
    }

    // Obtener estadísticas de reseñas
    @GetMapping("/stats")
    public ResponseEntity<?> getReviewStatistics() {
        Object stats = reviewServices.getReviewStatistics();
        return ResponseEntity.ok(stats);
    }

    // Obtener distribución de ratings por proveedor
    @GetMapping("/provider/{providerId}/distribution")
    public ResponseEntity<List<Object[]>> getProviderRatingDistribution(@PathVariable Long providerId) {
        List<Object[]> distribution = reviewServices.getProviderRatingDistribution(providerId);
        return ResponseEntity.ok(distribution);
    }

    // Buscar reseñas de alta calidad
    @GetMapping("/high-quality")
    public ResponseEntity<List<Review>> getHighQualityReviews(
            @RequestParam(defaultValue = "4") int minCleanliness,
            @RequestParam(defaultValue = "4") int minService) {
        List<Review> reviews = reviewServices.findHighQualityReviews(minCleanliness, minService);
        return ResponseEntity.ok(reviews);
    }

    // Buscar destinos de alta calidad
    @GetMapping("/destinations/high-quality")
    public ResponseEntity<List<DestinationRating>> getHighQualityDestinations(
            @RequestParam(defaultValue = "4.0") double minCleanliness,
            @RequestParam(defaultValue = "4.0") double minService) {
        List<DestinationRating> destinations = reviewServices.findHighQualityDestinations(minCleanliness, minService);
        return ResponseEntity.ok(destinations);
    }

    // Obtener promedio de ratings globales
    @GetMapping("/averages")
    public ResponseEntity<?> getAverageRatings() {
        Object averages = reviewServices.getAverageRatings();
        return ResponseEntity.ok(averages);
    }

    // Clases internas para requests
    public static class ResponseRequest {
        private String response;

        public String getResponse() { return response; }
        public void setResponse(String response) { this.response = response; }
    }

    public static class VerifyRequest {
        private String transactionHash;

        public String getTransactionHash() { return transactionHash; }
        public void setTransactionHash(String transactionHash) { this.transactionHash = transactionHash; }
    }
}