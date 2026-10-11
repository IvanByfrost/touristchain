package com.touristchain.kyoto.trip;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface TripRepository extends JpaRepository<TripEntity, Long> {

    /** Un destino vacío coincide con todo; los demás filtros nulos se ignoran. */
    @Query("""
            SELECT t FROM TripEntity t
            WHERE LOWER(t.destination) LIKE LOWER(CONCAT('%', :destination, '%'))
              AND (:status IS NULL OR t.status = :status)
              AND (:minPrice IS NULL OR t.price >= :minPrice)
              AND (:maxPrice IS NULL OR t.price <= :maxPrice)
            ORDER BY t.id
            """)
    List<TripEntity> search(@Param("destination") String destination,
                            @Param("status") String status,
                            @Param("minPrice") BigDecimal minPrice,
                            @Param("maxPrice") BigDecimal maxPrice);

    /** Bloquea la fila para que dos reservas simultáneas no vendan el mismo cupo. */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT t FROM TripEntity t WHERE t.id = :id")
    Optional<TripEntity> findForUpdate(@Param("id") Long id);
}
