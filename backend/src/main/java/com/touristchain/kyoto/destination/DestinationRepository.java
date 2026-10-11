package com.touristchain.kyoto.destination;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DestinationRepository extends JpaRepository<Destination, Long> {

        /** Los filtros vacíos se ignoran; la comparación no distingue mayúsculas. */
        @Query("SELECT d FROM Destination d WHERE " +
                        "(:includeInactive = true OR d.active = true) AND " +
                        "(:country = '' OR LOWER(d.country) = LOWER(:country)) AND " +
                        "(:city = '' OR LOWER(d.city) = LOWER(:city)) AND " +
                        "(:category = '' OR LOWER(d.category) = LOWER(:category))")
        Page<Destination> findWithFilters(
                        @Param("country") String country,
                        @Param("city") String city,
                        @Param("category") String category,
                        @Param("includeInactive") boolean includeInactive,
                        Pageable pageable);

        @Query("SELECT d FROM Destination d WHERE d.active = true AND (" +
                        "LOWER(d.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
                        "LOWER(d.description) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
                        "EXISTS (SELECT t FROM d.tags t WHERE LOWER(t) LIKE LOWER(CONCAT('%', :query, '%')))) " +
                        "ORDER BY d.id")
        List<Destination> searchActive(@Param("query") String query);

        Page<Destination> findByActiveTrueOrderByRatingDescTotalReviewsDesc(Pageable pageable);

        long countByActiveTrue();
}
