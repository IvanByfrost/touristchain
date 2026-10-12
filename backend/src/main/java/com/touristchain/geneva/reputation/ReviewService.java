package com.touristchain.geneva.reputation;

import com.touristchain.common.NotFoundException;
import com.touristchain.geneva.partners.PartnerEntity;
import com.touristchain.geneva.partners.PartnerRepository;
import com.touristchain.geneva.tourists.TouristEntity;
import com.touristchain.geneva.tourists.TouristRepository;
import com.touristchain.geneva.users.UserEntity;
import com.touristchain.geneva.users.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {

    /** Tipos de ítem del catálogo que se pueden reseñar. */
    private static final Set<String> ITEM_TYPES = Set.of("viaje", "hotel", "car", "renta", "paquete");

    private final ReviewRepository reviewRepository;
    private final TouristRepository touristRepository;
    private final UserRepository userRepository;
    private final PartnerRepository partnerRepository;

    @Transactional(readOnly = true)
    public List<ReviewView> getReviews(String itemType, String itemId) {
        List<ReviewEntity> reviews;
        if (isBlank(itemType)) {
            reviews = reviewRepository.findAllByOrderByCreatedAtDesc();
        } else if (isBlank(itemId)) {
            reviews = reviewRepository.findByItemTypeOrderByCreatedAtDesc(itemType);
        } else {
            reviews = reviewRepository.findByItemTypeAndItemRefOrderByCreatedAtDesc(itemType, itemId.trim());
        }
        return toViews(reviews);
    }

    @Transactional(readOnly = true)
    public ReviewStats getStats(String itemType, String itemId) {
        List<ReviewView> reviews = getReviews(itemType, itemId);
        double average = reviews.stream().mapToInt(ReviewView::rating).average().orElse(0);
        return new ReviewStats(Math.round(average * 10) / 10.0, reviews.size());
    }

    @Transactional(readOnly = true)
    public List<ReviewView> getReviewsByUser(UUID userId) {
        return touristRepository.findByUserId(userId)
                .map(tourist -> toViews(reviewRepository.findByTouristIdOrderByCreatedAtDesc(tourist.getTouristId())))
                .orElse(List.of());
    }

    @Transactional(readOnly = true)
    public List<ReviewView> getReviewsByPartner(UUID partnerId) {
        return toViews(reviewRepository.findByPartnerIdOrderByCreatedAtDesc(partnerId));
    }

    @Transactional(readOnly = true)
    public List<ReviewView> getReviewsByPackage(UUID packageId) {
        return toViews(reviewRepository.findByPackageIdOrderByCreatedAtDesc(packageId));
    }

    @Transactional
    public ReviewView createReview(UUID userId, CreateReviewRequest request) {
        TouristEntity tourist = touristRepository.findByUserId(userId)
                .orElseThrow(() -> new SecurityException("Solo los turistas pueden publicar reseñas"));
        UUID touristId = tourist.getTouristId();

        if (request.rating() == null || request.rating() < 1 || request.rating() > 5) {
            throw new IllegalArgumentException("La calificación debe estar entre 1 y 5");
        }
        String itemType = blankToNull(request.itemType());
        String itemRef = blankToNull(request.itemId());
        if ((itemType == null) != (itemRef == null)) {
            throw new IllegalArgumentException("El tipo y el ID del ítem deben indicarse juntos");
        }
        if (itemType != null && !ITEM_TYPES.contains(itemType)) {
            throw new IllegalArgumentException("Tipo de ítem no válido: " + itemType);
        }
        if (itemRef == null && request.partnerId() == null && request.packageId() == null) {
            throw new IllegalArgumentException("Indica qué se está reseñando");
        }
        boolean duplicate = itemRef != null
                ? reviewRepository.existsByTouristIdAndItemTypeAndItemRef(touristId, itemType, itemRef)
                : request.partnerId() != null && request.packageId() == null
                        && reviewRepository.existsByTouristIdAndPartnerIdAndItemRefIsNull(touristId,
                                request.partnerId());
        if (duplicate) {
            throw new IllegalStateException("Ya has reseñado este ítem");
        }

        ReviewEntity review = new ReviewEntity();
        review.setTouristId(touristId);
        review.setPartnerId(request.partnerId());
        review.setPackageId(request.packageId());
        review.setBookingId(request.bookingId());
        review.setItemType(itemType);
        review.setItemRef(itemRef);
        review.setItemName(blankToNull(request.itemName()));
        review.setRating(request.rating());
        review.setComment(request.comment());

        return toView(reviewRepository.save(review));
    }

    @Transactional
    public ReviewView updateReview(UUID reviewId, UUID userId, UpdateReviewRequest request) {
        ReviewEntity review = getEditableReview(reviewId, userId, false);
        if (request.rating() != null) {
            if (request.rating() < 1 || request.rating() > 5) {
                throw new IllegalArgumentException("La calificación debe estar entre 1 y 5");
            }
            review.setRating(request.rating());
        }
        if (request.comment() != null) {
            review.setComment(request.comment());
        }
        return toView(reviewRepository.save(review));
    }

    @Transactional
    public void deleteReview(UUID reviewId, UUID userId, boolean isAdmin) {
        reviewRepository.delete(getEditableReview(reviewId, userId, isAdmin));
    }

    /** El socio responde las reseñas de su empresa (una sola vez); un administrador, cualquiera. */
    @Transactional
    public ReviewView respond(UUID reviewId, UUID userId, boolean isAdmin, String response) {
        if (isBlank(response)) {
            throw new IllegalArgumentException("La respuesta no puede estar vacía");
        }
        ReviewEntity review = findReview(reviewId);
        if (!isAdmin) {
            boolean ownsPartner = review.getPartnerId() != null && partnerRepository.findByUserId(userId)
                    .map(partner -> partner.getPartnerId().equals(review.getPartnerId()))
                    .orElse(false);
            if (!ownsPartner) {
                throw new SecurityException("Solo el socio reseñado puede responder esta reseña");
            }
        }
        if (review.getResponse() != null) {
            throw new IllegalStateException("Esta reseña ya tiene una respuesta");
        }
        review.setResponse(response.trim());
        review.setRespondedAt(LocalDateTime.now());
        return toView(reviewRepository.save(review));
    }

    @Transactional
    public ReviewView verify(UUID reviewId) {
        ReviewEntity review = findReview(reviewId);
        if (Boolean.TRUE.equals(review.getVerified())) {
            throw new IllegalStateException("La reseña ya está verificada");
        }
        review.setVerified(true);
        return toView(reviewRepository.save(review));
    }

    @Transactional(readOnly = true)
    public PartnerReputation getPartnerReputation(UUID partnerId) {
        if (!partnerRepository.existsById(partnerId)) {
            throw new NotFoundException("Socio no encontrado");
        }
        long total = reviewRepository.countReviewsForPartner(partnerId);
        Double average = reviewRepository.calculateAverageRatingForPartner(partnerId);
        Map<Integer, Long> distribution = new LinkedHashMap<>();
        for (int stars = 5; stars >= 1; stars--) {
            distribution.put(stars, 0L);
        }
        for (Object[] row : reviewRepository.countRatingsByValueForPartner(partnerId)) {
            distribution.put((Integer) row[0], (Long) row[1]);
        }
        double responseRate = total == 0 ? 0
                : reviewRepository.countRespondedForPartner(partnerId) * 100.0 / total;
        return new PartnerReputation(partnerId, round1(average == null ? 0 : average), total, distribution,
                round1(responseRate));
    }

    /** Ranking de socios por promedio; exige un mínimo de reseñas para que una sola no decida. */
    @Transactional(readOnly = true)
    public List<TopPartner> getTopPartners(int limit, int minReviews) {
        List<Object[]> rows = reviewRepository.rankPartners(Math.max(minReviews, 1)).stream()
                .limit(Math.min(Math.max(limit, 1), 50))
                .toList();
        Map<UUID, String> names = partnerRepository.findAllById(rows.stream().map(row -> (UUID) row[0]).toList())
                .stream().collect(Collectors.toMap(PartnerEntity::getPartnerId, PartnerEntity::getCompanyName));
        return rows.stream()
                .map(row -> new TopPartner((UUID) row[0], names.get((UUID) row[0]),
                        round1((Double) row[1]), (Long) row[2]))
                .toList();
    }

    private ReviewEntity findReview(UUID reviewId) {
        return reviewRepository.findById(reviewId)
                .orElseThrow(() -> new NotFoundException("Reseña no encontrada"));
    }

    private static double round1(double value) {
        return Math.round(value * 10) / 10.0;
    }

    /** La reseña solo la modifica su autor; un administrador puede eliminarla. */
    private ReviewEntity getEditableReview(UUID reviewId, UUID userId, boolean isAdmin) {
        ReviewEntity review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Reseña no encontrada"));
        if (isAdmin) {
            return review;
        }
        boolean isAuthor = touristRepository.findByUserId(userId)
                .map(tourist -> tourist.getTouristId().equals(review.getTouristId()))
                .orElse(false);
        if (!isAuthor) {
            throw new SecurityException("No tienes permiso para modificar esta reseña");
        }
        return review;
    }

    private ReviewView toView(ReviewEntity review) {
        return toViews(List.of(review)).get(0);
    }

    /** Resuelve los autores (turista -> usuario) con dos consultas en lote. */
    private List<ReviewView> toViews(List<ReviewEntity> reviews) {
        Set<UUID> touristIds = reviews.stream().map(ReviewEntity::getTouristId).collect(Collectors.toSet());
        Map<UUID, UUID> userIdByTourist = touristRepository.findAllById(touristIds).stream()
                .collect(Collectors.toMap(TouristEntity::getTouristId, TouristEntity::getUserId));
        Map<UUID, UserEntity> users = userRepository.findAllById(userIdByTourist.values()).stream()
                .collect(Collectors.toMap(UserEntity::getUserId, Function.identity()));
        return reviews.stream()
                .map(review -> ReviewView.from(review, users.get(userIdByTourist.get(review.getTouristId()))))
                .toList();
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private static String blankToNull(String value) {
        return isBlank(value) ? null : value.trim();
    }

    public record ReviewStats(double average, int count) {
    }

    public record CreateReviewRequest(
            String itemType,
            String itemId,
            String itemName,
            UUID partnerId,
            UUID packageId,
            UUID bookingId,
            Integer rating,
            String comment) {
    }

    public record PartnerReputation(
            UUID partnerId,
            double average,
            long total,
            Map<Integer, Long> distribution,
            double responseRate) {
    }

    public record TopPartner(UUID partnerId, String companyName, double average, long total) {
    }

    public record RespondRequest(String response) {
    }

    public record UpdateReviewRequest(
            Integer rating,
            String comment) {
    }
}
