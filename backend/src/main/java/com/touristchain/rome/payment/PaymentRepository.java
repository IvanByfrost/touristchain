package com.touristchain.rome.payment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<PaymentEntity, UUID> {

    List<PaymentEntity> findAllByOrderByCreatedAtAsc();

    List<PaymentEntity> findByUserIdOrderByCreatedAtAsc(UUID userId);

    List<PaymentEntity> findByUserIdAndStatus(UUID userId, String status);

    Optional<PaymentEntity> findByQrToken(String qrToken);

    List<PaymentEntity> findByStatusAndCreatedAtBefore(String status, LocalDateTime threshold);

    List<PaymentEntity> findByEscrowStatusOrderByPaidAtAsc(String escrowStatus);

    @Query("SELECT p.status, COUNT(p), COALESCE(SUM(p.amount), 0) FROM PaymentEntity p GROUP BY p.status")
    List<Object[]> summarizeByStatus();

    @Query("SELECT p.escrowStatus, COUNT(p), COALESCE(SUM(p.amount), 0) FROM PaymentEntity p "
            + "WHERE p.escrowStatus IS NOT NULL GROUP BY p.escrowStatus")
    List<Object[]> summarizeByEscrowStatus();
}
