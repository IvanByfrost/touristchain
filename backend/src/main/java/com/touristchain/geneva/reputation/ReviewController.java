package com.touristchain.geneva.reputation;

import com.touristchain.geneva.reputation.ReviewService.*;
import com.touristchain.geneva.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    /** GET /api/v1/reviews?itemType=&itemId= (público) */
    @GetMapping
    public List<ReviewView> getReviews(@RequestParam(required = false) String itemType,
            @RequestParam(required = false) String itemId) {
        return reviewService.getReviews(itemType, itemId);
    }

    /** GET /api/v1/reviews/stats?itemType=&itemId= (público) */
    @GetMapping("/stats")
    public ReviewStats getStats(@RequestParam(required = false) String itemType,
            @RequestParam(required = false) String itemId) {
        return reviewService.getStats(itemType, itemId);
    }

    @GetMapping("/me")
    public List<ReviewView> getMyReviews(@AuthenticationPrincipal UserPrincipal principal) {
        return reviewService.getReviewsByUser(principal.getUserId());
    }

    @GetMapping("/partner/{partnerId}")
    public List<ReviewView> getReviewsByPartner(@PathVariable UUID partnerId) {
        return reviewService.getReviewsByPartner(partnerId);
    }

    @GetMapping("/package/{packageId}")
    public List<ReviewView> getReviewsByPackage(@PathVariable UUID packageId) {
        return reviewService.getReviewsByPackage(packageId);
    }

    @PostMapping
    public ResponseEntity<ReviewView> createReview(@AuthenticationPrincipal UserPrincipal principal,
            @RequestBody CreateReviewRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(reviewService.createReview(principal.getUserId(), request));
    }

    @PutMapping("/{reviewId}")
    public ReviewView updateReview(@AuthenticationPrincipal UserPrincipal principal,
            @PathVariable UUID reviewId, @RequestBody UpdateReviewRequest request) {
        return reviewService.updateReview(reviewId, principal.getUserId(), request);
    }

    @DeleteMapping("/{reviewId}")
    public Map<String, String> deleteReview(@AuthenticationPrincipal UserPrincipal principal,
            @PathVariable UUID reviewId) {
        boolean isAdmin = principal.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_ADMIN".equals(authority.getAuthority()));
        reviewService.deleteReview(reviewId, principal.getUserId(), isAdmin);
        return Map.of("message", "Reseña eliminada");
    }
}
