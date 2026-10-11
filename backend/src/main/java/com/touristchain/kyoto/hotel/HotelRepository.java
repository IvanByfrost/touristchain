package com.touristchain.kyoto.hotel;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface HotelRepository extends JpaRepository<HotelEntity, Long> {

    /** Los filtros de texto vacíos coinciden con todo; los numéricos nulos se ignoran. */
    @Query("""
            SELECT h FROM HotelEntity h
            WHERE LOWER(h.city) LIKE LOWER(CONCAT('%', :city, '%'))
              AND (LOWER(h.name) LIKE LOWER(CONCAT('%', :search, '%'))
                   OR LOWER(h.city) LIKE LOWER(CONCAT('%', :search, '%')))
              AND (:stars IS NULL OR h.stars = :stars)
              AND (:minPrice IS NULL OR h.price >= :minPrice)
              AND (:maxPrice IS NULL OR h.price <= :maxPrice)
            ORDER BY h.id
            """)
    List<HotelEntity> search(@Param("city") String city,
                             @Param("search") String search,
                             @Param("stars") Integer stars,
                             @Param("minPrice") BigDecimal minPrice,
                             @Param("maxPrice") BigDecimal maxPrice);
}
