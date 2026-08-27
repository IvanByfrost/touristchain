package com.touristchain.geneva.reputation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ReviewRepository extends JpaRepository<ReviewEntity, UUID> {

    List<ReviewEntity> findByTouristId(UUID touristId);

    List<ReviewEntity> findByPartnerId(UUID partnerId);

    List<ReviewEntity> findByPackageId(UUID packageId);

    @Query("SELECT AVG(r.rating) FROM ReviewEntity r WHERE r.partnerId = :partnerId")
    Double calculateAverageRatingForPartner(@Param("partnerId") UUID partnerId);

    @Query("SELECT COUNT(r) FROM ReviewEntity r WHERE r.partnerId = :partnerId")
    Long countReviewsForPartner(@Param("partnerId") UUID partnerId);

    @Query("SELECT r.rating, COUNT(r) FROM ReviewEntity r WHERE r.partnerId = :partnerId GROUP BY r.rating")
    List<Object[]> countRatingsByValueForPartner(@Param("partnerId") UUID partnerId);

    boolean existsByTouristIdAndPartnerId(UUID touristId, UUID partnerId);
}