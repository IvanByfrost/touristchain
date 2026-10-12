package com.touristchain.geneva.reputation;

import com.touristchain.geneva.users.UserEntity;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.UUID;

/** Reseña con los datos del autor, en el formato que usa el frontend. */
public record ReviewView(
        UUID id,
        UUID userId,
        String userName,
        String avatar,
        String itemType,
        String itemId,
        String itemName,
        UUID partnerId,
        UUID packageId,
        Integer rating,
        String comment,
        String response,
        LocalDateTime respondedAt,
        boolean verified,
        LocalDateTime createdAt) {

    static ReviewView from(ReviewEntity review, UserEntity author) {
        String userName = author == null ? "Turista"
                : author.getLastName() == null ? author.getFirstName()
                : author.getFirstName() + " " + author.getLastName();
        String seed = author == null ? review.getTouristId().toString() : author.getEmail();
        return new ReviewView(
                review.getReviewId(),
                author == null ? null : author.getUserId(),
                userName,
                "https://api.dicebear.com/7.x/avataaars/svg?seed=" + URLEncoder.encode(seed, StandardCharsets.UTF_8),
                review.getItemType(),
                review.getItemRef(),
                review.getItemName(),
                review.getPartnerId(),
                review.getPackageId(),
                review.getRating(),
                review.getComment(),
                review.getResponse(),
                review.getRespondedAt(),
                Boolean.TRUE.equals(review.getVerified()),
                review.getCreatedAt());
    }
}
