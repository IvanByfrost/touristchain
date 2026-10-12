package com.touristchain.rome.payment;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<PaymentEntity, UUID> {

    List<PaymentEntity> findAllByOrderByCreatedAtAsc();

    List<PaymentEntity> findByUserIdOrderByCreatedAtAsc(UUID userId);

    List<PaymentEntity> findByUserIdAndStatus(UUID userId, String status);

    Optional<PaymentEntity> findByQrToken(String qrToken);
}
