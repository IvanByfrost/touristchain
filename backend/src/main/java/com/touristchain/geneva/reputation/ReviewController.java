package com.touristchain.geneva.reputation;

import com.touristchain.geneva.reputation.ReviewService.*;
import com.touristchain.geneva.users.UserEntity;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<ReviewEntity> createReview(@Valid @RequestBody CreateReviewRequest request) {
        // El touristId lo obtenemos del token y lo inyectamos en el request
        UUID touristId = getCurrentTouristId();
        CreateReviewRequest requestWithTourist = new CreateReviewRequest(
                touristId,
                request.partnerId(),
                request.packageId(),
                request.bookingId(),
                request.rating(),
                request.comment());
        ReviewEntity created = reviewService.createReview(requestWithTourist);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{reviewId}")
    public ResponseEntity<ReviewEntity> getReviewById(@PathVariable UUID reviewId) {
        ReviewEntity review = reviewService.getReviewById(reviewId);
        return ResponseEntity.ok(review);
    }

    @GetMapping("/partner/{partnerId}")
    public ResponseEntity<List<ReviewEntity>> getReviewsByPartner(@PathVariable UUID partnerId) {
        List<ReviewEntity> reviews = reviewService.getReviewsByPartner(partnerId);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/package/{packageId}")
    public ResponseEntity<List<ReviewEntity>> getReviewsByPackage(@PathVariable UUID packageId) {
        List<ReviewEntity> reviews = reviewService.getReviewsByPackage(packageId);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/me")
    public ResponseEntity<List<ReviewEntity>> getMyReviews() {
        UUID touristId = getCurrentTouristId();
        List<ReviewEntity> reviews = reviewService.getReviewsByTourist(touristId);
        return ResponseEntity.ok(reviews);
    }

    @PutMapping("/{reviewId}")
    public ResponseEntity<ReviewEntity> updateReview(
            @PathVariable UUID reviewId,
            @Valid @RequestBody UpdateReviewRequest request) {
        UUID currentUserId = getCurrentUserId();
        ReviewEntity updated = reviewService.updateReview(reviewId, currentUserId, request);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> deleteReview(@PathVariable UUID reviewId) {
        UUID currentUserId = getCurrentUserId();
        reviewService.deleteReview(reviewId, currentUserId);
        return ResponseEntity.noContent().build();
    }

    // ========== HELPERS ==========

    private UUID getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("Usuario no autenticado");
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserEntity userEntity) {
            return userEntity.getUserId();
        }
        throw new IllegalStateException("No se pudo obtener el userId del token");
    }

    private UUID getCurrentTouristId() {
        // En una implementación real, obtendrías el touristId asociado al userId
        // Por ahora, asumimos que el userId es el mismo que touristId
        return getCurrentUserId();
    }
}