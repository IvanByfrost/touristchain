package com.touristchain.geneva.reputation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ReviewRepository extends JpaRepository<ReviewEntity, UUID> {

    List<ReviewEntity> findAllByOrderByCreatedAtDesc();

    List<ReviewEntity> findByItemTypeOrderByCreatedAtDesc(String itemType);

    List<ReviewEntity> findByItemTypeAndItemRefOrderByCreatedAtDesc(String itemType, String itemRef);

    List<ReviewEntity> findByTouristIdOrderByCreatedAtDesc(UUID touristId);

    List<ReviewEntity> findByPartnerIdOrderByCreatedAtDesc(UUID partnerId);

    List<ReviewEntity> findByPackageIdOrderByCreatedAtDesc(UUID packageId);

    @Query("SELECT AVG(r.rating) FROM ReviewEntity r WHERE r.partnerId = :partnerId")
    Double calculateAverageRatingForPartner(@Param("partnerId") UUID partnerId);

    @Query("SELECT COUNT(r) FROM ReviewEntity r WHERE r.partnerId = :partnerId")
    Long countReviewsForPartner(@Param("partnerId") UUID partnerId);

    @Query("SELECT r.rating, COUNT(r) FROM ReviewEntity r WHERE r.partnerId = :partnerId GROUP BY r.rating")
    List<Object[]> countRatingsByValueForPartner(@Param("partnerId") UUID partnerId);

    @Query("SELECT COUNT(r) FROM ReviewEntity r WHERE r.partnerId = :partnerId AND r.response IS NOT NULL")
    Long countRespondedForPartner(@Param("partnerId") UUID partnerId);

    /** Socios con al menos {@code minReviews} reseñas: [partnerId, promedio, total], mejor promedio primero. */
    @Query("""
            SELECT r.partnerId, AVG(r.rating), COUNT(r) FROM ReviewEntity r
            WHERE r.partnerId IS NOT NULL
            GROUP BY r.partnerId
            HAVING COUNT(r) >= :minReviews
            ORDER BY AVG(r.rating) DESC, COUNT(r) DESC
            """)
    List<Object[]> rankPartners(@Param("minReviews") long minReviews);

    boolean existsByTouristIdAndPartnerIdAndItemRefIsNull(UUID touristId, UUID partnerId);

    boolean existsByTouristIdAndItemTypeAndItemRef(UUID touristId, String itemType, String itemRef);
}
