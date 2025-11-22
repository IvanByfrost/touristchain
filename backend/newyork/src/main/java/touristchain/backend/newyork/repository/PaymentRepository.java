package com.touristchain.backend.newyork.repositories;

import com.touristchain.backend.newyork.models.PaymentModels.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    
    // Buscar pagos por bookingId
    List<Payment> findByBookingId(Long bookingId);
    
    // Buscar pagos por wallet del turista
    List<Payment> findByTouristWallet(String touristWallet);
    
    // Buscar pagos por wallet del proveedor
    List<Payment> findByProviderWallet(String providerWallet);
    
    // Buscar pagos por estado
    List<Payment> findByStatus(String status);
    
    // Buscar pagos por tipo
    List<Payment> findByPaymentType(String paymentType);
    
    // Buscar pago por transaction hash de deposito
    Optional<Payment> findByDepositTxHash(String depositTxHash);
    
    // Buscar pago por transaction hash de liberacion
    Optional<Payment> findByReleaseTxHash(String releaseTxHash);
    
    // Buscar pago por transaction hash de reembolso
    Optional<Payment> findByRefundTxHash(String refundTxHash);
    
    // Buscar pagos por contrato address
    Optional<Payment> findByContractAddress(String contractAddress);
    
    // Buscar pagos pendientes de un turista
    List<Payment> findByTouristWalletAndStatus(String touristWallet, String status);
    
    // Buscar pagos pendientes de un proveedor
    List<Payment> findByProviderWalletAndStatus(String providerWallet, String status);
    
    // Verificar si existe pago para un booking
    boolean existsByBookingId(Long bookingId);
    
    // Buscar pagos por rango de monto
    List<Payment> findByAmountBetween(Double minAmount, Double maxAmount);
    
    // Buscar pagos escrow activos
    @Query("SELECT p FROM Payment p WHERE p.paymentType = 'ESCROW' AND p.status IN ('PENDING', 'DEPOSITED')")
    List<Payment> findActiveEscrowPayments();
    
    // Buscar pagos que necesitan confirmacion (depositados pero no liberados/reembolsados)
    @Query("SELECT p FROM Payment p WHERE p.status = 'DEPOSITED' AND p.depositedAt IS NOT NULL")
    List<Payment> findPaymentsReadyForRelease();
    
    // Obtener estadisticas de pagos por estado
    @Query("SELECT p.status, COUNT(p), SUM(p.amount) FROM Payment p GROUP BY p.status")
    List<Object[]> getPaymentStatsByStatus();
    
    // Obtener total de fondos en escrow
    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.status = 'DEPOSITED'")
    Double getTotalEscrowAmount();
    
    // Buscar pagos por bookingId y estado
    List<Payment> findByBookingIdAndStatus(Long bookingId, String status);
    
    // Buscar pagos recientes de un turista
    List<Payment> findByTouristWalletOrderByCreatedAtDesc(String touristWallet);
    
    // Buscar pagos recientes de un proveedor
    List<Payment> findByProviderWalletOrderByCreatedAtDesc(String providerWallet);
    
    // Verificar si existe transaccion duplicada
    @Query("SELECT COUNT(p) > 0 FROM Payment p WHERE p.depositTxHash = :txHash OR p.releaseTxHash = :txHash OR p.refundTxHash = :txHash")
    boolean existsByTransactionHash(@Param("txHash") String txHash);
    
    // Buscar pagos expirados (pendientes por mucho tiempo)
    @Query("SELECT p FROM Payment p WHERE p.status = 'PENDING' AND p.createdAt < :expirationTime")
    List<Payment> findExpiredPendingPayments(@Param("expirationTime") java.time.LocalDateTime expirationTime);
}