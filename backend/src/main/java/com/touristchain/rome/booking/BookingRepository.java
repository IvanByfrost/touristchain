package com.touristchain.rome.booking;

import com.touristchain.rome.booking.BookingEntity.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface BookingRepository extends JpaRepository<BookingEntity, UUID> {

    List<BookingEntity> findAllByOrderByCreatedAtAsc();

    List<BookingEntity> findByTouristIdOrderByCreatedAtAsc(UUID touristId);

    List<BookingEntity> findByPartnerIdOrderByCreatedAtAsc(UUID partnerId);

    List<BookingEntity> findByTouristIdAndStatusOrderByCreatedAtAsc(UUID touristId, BookingStatus status);

    List<BookingEntity> findByPaymentIdAndStatus(UUID paymentId, BookingStatus status);
}
