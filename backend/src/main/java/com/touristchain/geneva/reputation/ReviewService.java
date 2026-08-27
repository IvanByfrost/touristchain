package com.touristchain.geneva.reputation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;

    @Transactional
    public ReviewEntity createReview(CreateReviewRequest request) {
        // Validar que touristId venga en el request
        if (request.touristId() == null) {
            throw new IllegalArgumentException("El ID del turista es obligatorio");
        }

        ReviewEntity review = new ReviewEntity();
        review.setReviewId(UUID.randomUUID());
        review.setTouristId(request.touristId());
        review.setPartnerId(request.partnerId());
        review.setPackageId(request.packageId());
        review.setBookingId(request.bookingId());
        review.setRating(request.rating());
        review.setComment(request.comment());

        return reviewRepository.save(review);
    }

    @Transactional(readOnly = true)
    public ReviewEntity getReviewById(UUID reviewId) {
        return reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Reseña no encontrada"));
    }

    @Transactional(readOnly = true)
    public List<ReviewEntity> getReviewsByTourist(UUID touristId) {
        return reviewRepository.findByTouristId(touristId);
    }

    @Transactional(readOnly = true)
    public List<ReviewEntity> getReviewsByPartner(UUID partnerId) {
        return reviewRepository.findByPartnerId(partnerId);
    }

    @Transactional(readOnly = true)
    public List<ReviewEntity> getReviewsByPackage(UUID packageId) {
        return reviewRepository.findByPackageId(packageId);
    }

    @Transactional(readOnly = true)
    public Double getAverageRatingForPartner(UUID partnerId) {
        return reviewRepository.calculateAverageRatingForPartner(partnerId);
    }

    @Transactional(readOnly = true)
    public Long getTotalReviewsForPartner(UUID partnerId) {
        return reviewRepository.countReviewsForPartner(partnerId);
    }

    @Transactional
    public ReviewEntity updateReview(UUID reviewId, UUID currentUserId, UpdateReviewRequest request) {
        ReviewEntity review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Reseña no encontrada"));

        // Validar que el usuario sea el autor o un administrador
        if (!review.getTouristId().equals(currentUserId)) {
            // Aquí podrías verificar si el usuario tiene rol ADMIN
            throw new SecurityException("No tienes permiso para modificar esta reseña");
        }

        if (request.rating() != null) {
            review.setRating(request.rating());
        }
        if (request.comment() != null) {
            review.setComment(request.comment());
        }

        return reviewRepository.save(review);
    }

    @Transactional
    public void deleteReview(UUID reviewId, UUID currentUserId) {
        ReviewEntity review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Reseña no encontrada"));

        // Validar que el usuario sea el autor o un administrador
        if (!review.getTouristId().equals(currentUserId)) {
            throw new SecurityException("No tienes permiso para eliminar esta reseña");
        }

        reviewRepository.deleteById(reviewId);
    }

    @Transactional(readOnly = true)
    public boolean hasUserReviewedPartner(UUID touristId, UUID partnerId) {
        return reviewRepository.existsByTouristIdAndPartnerId(touristId, partnerId);
    }

    public record CreateReviewRequest(
            UUID touristId,
            UUID partnerId,
            UUID packageId,
            UUID bookingId,
            Integer rating,
            String comment) {
    }

    public record UpdateReviewRequest(
            Integer rating,
            String comment) {
    }
}