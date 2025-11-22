package com.touristchain.backend.newyork.services;

import com.touristchain.backend.newyork.models.PaymentModels.Payment;
import com.touristchain.backend.newyork.repositories.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PaymentServices {

    @Autowired
    private PaymentRepository paymentRepository;

    // Crear nuevo pago escrow
    public Payment createEscrowPayment(Long bookingId, String touristWallet, String providerWallet, Double amount) {
        // Validar parametros
        if (bookingId == null) {
            throw new IllegalArgumentException("El ID de reserva es requerido");
        }
        if (touristWallet == null || touristWallet.trim().isEmpty()) {
            throw new IllegalArgumentException("La wallet del turista es requerida");
        }
        if (providerWallet == null || providerWallet.trim().isEmpty()) {
            throw new IllegalArgumentException("La wallet del proveedor es requerida");
        }
        if (amount == null || amount <= 0) {
            throw new IllegalArgumentException("El monto debe ser mayor a cero");
        }

        // Verificar si ya existe pago para esta reserva
        if (paymentRepository.existsByBookingId(bookingId)) {
            throw new IllegalArgumentException("Ya existe un pago para esta reserva");
        }

        Payment payment = new Payment(bookingId, touristWallet, providerWallet, amount);
        return paymentRepository.save(payment);
    }

    // Obtener pago por ID
    public Optional<Payment> findById(Long id) {
        return paymentRepository.findById(id);
    }

    // Obtener pagos por bookingId
    public List<Payment> findByBookingId(Long bookingId) {
        return paymentRepository.findByBookingId(bookingId);
    }

    // Obtener pagos por turista
    public List<Payment> findByTouristWallet(String touristWallet) {
        return paymentRepository.findByTouristWallet(touristWallet);
    }

    // Obtener pagos por proveedor
    public List<Payment> findByProviderWallet(String providerWallet) {
        return paymentRepository.findByProviderWallet(providerWallet);
    }

    // Obtener pagos por estado
    public List<Payment> findByStatus(String status) {
        return paymentRepository.findByStatus(status);
    }

    // Marcar pago como depositado (cuando se ejecuta el smart contract)
    public Optional<Payment> markAsDeposited(Long id, String contractAddress, String depositTxHash) {
        Optional<Payment> paymentOpt = paymentRepository.findById(id);
        
        if (paymentOpt.isPresent()) {
            Payment payment = paymentOpt.get();
            
            if (!payment.isPending()) {
                throw new IllegalStateException("Solo se pueden depositar pagos pendientes");
            }

            // Verificar que no exista transaccion duplicada
            if (paymentRepository.existsByTransactionHash(depositTxHash)) {
                throw new IllegalArgumentException("La transaccion de deposito ya existe");
            }

            payment.setStatus("DEPOSITED");
            payment.setContractAddress(contractAddress);
            payment.setDepositTxHash(depositTxHash);
            payment.setDepositedAt(LocalDateTime.now());
            payment.setUpdatedAt(LocalDateTime.now());

            return Optional.of(paymentRepository.save(payment));
        }
        
        return Optional.empty();
    }

    // Liberar fondos al proveedor
    public Optional<Payment> releaseToProvider(Long id, String releaseTxHash) {
        Optional<Payment> paymentOpt = paymentRepository.findById(id);
        
        if (paymentOpt.isPresent()) {
            Payment payment = paymentOpt.get();
            
            if (!payment.isDeposited()) {
                throw new IllegalStateException("Solo se pueden liberar pagos depositados");
            }

            // Verificar que no exista transaccion duplicada
            if (paymentRepository.existsByTransactionHash(releaseTxHash)) {
                throw new IllegalArgumentException("La transaccion de liberacion ya existe");
            }

            payment.setStatus("RELEASED");
            payment.setReleaseTxHash(releaseTxHash);
            payment.setReleasedAt(LocalDateTime.now());
            payment.setUpdatedAt(LocalDateTime.now());

            return Optional.of(paymentRepository.save(payment));
        }
        
        return Optional.empty();
    }

    // Reembolsar fondos al turista
    public Optional<Payment> refundToTourist(Long id, String refundTxHash) {
        Optional<Payment> paymentOpt = paymentRepository.findById(id);
        
        if (paymentOpt.isPresent()) {
            Payment payment = paymentOpt.get();
            
            if (!payment.isDeposited()) {
                throw new IllegalStateException("Solo se pueden reembolsar pagos depositados");
            }

            // Verificar que no exista transaccion duplicada
            if (paymentRepository.existsByTransactionHash(refundTxHash)) {
                throw new IllegalArgumentException("La transaccion de reembolso ya existe");
            }

            payment.setStatus("REFUNDED");
            payment.setRefundTxHash(refundTxHash);
            payment.setRefundedAt(LocalDateTime.now());
            payment.setUpdatedAt(LocalDateTime.now());

            return Optional.of(paymentRepository.save(payment));
        }
        
        return Optional.empty();
    }

    // Cancelar pago
    public Optional<Payment> cancelPayment(Long id) {
        Optional<Payment> paymentOpt = paymentRepository.findById(id);
        
        if (paymentOpt.isPresent()) {
            Payment payment = paymentOpt.get();
            
            if (!payment.isPending()) {
                throw new IllegalStateException("Solo se pueden cancelar pagos pendientes");
            }

            payment.setStatus("CANCELLED");
            payment.setUpdatedAt(LocalDateTime.now());

            return Optional.of(paymentRepository.save(payment));
        }
        
        return Optional.empty();
    }

    // Obtener pagos escrow activos
    public List<Payment> getActiveEscrowPayments() {
        return paymentRepository.findActiveEscrowPayments();
    }

    // Obtener pagos listos para liberar
    public List<Payment> getPaymentsReadyForRelease() {
        return paymentRepository.findPaymentsReadyForRelease();
    }

    // Verificar estado de pago por bookingId
    public Optional<Payment> getPaymentByBookingId(Long bookingId) {
        List<Payment> payments = paymentRepository.findByBookingId(bookingId);
        return payments.isEmpty() ? Optional.empty() : Optional.of(payments.get(0));
    }

    // Obtener estadisticas de pagos
    public Object getPaymentStatistics() {
        long totalPayments = paymentRepository.count();
        long pendingPayments = paymentRepository.findByStatus("PENDING").size();
        long depositedPayments = paymentRepository.findByStatus("DEPOSITED").size();
        long releasedPayments = paymentRepository.findByStatus("RELEASED").size();
        long refundedPayments = paymentRepository.findByStatus("REFUNDED").size();
        
        Double totalEscrow = paymentRepository.getTotalEscrowAmount();
        
        return new Object() {
            public final long total = totalPayments;
            public final long pending = pendingPayments;
            public final long deposited = depositedPayments;
            public final long released = releasedPayments;
            public final long refunded = refundedPayments;
            public final Double totalEscrowAmount = totalEscrow;
        };
    }

    // Calcular total de fondos liberados
    public Double calculateTotalReleased() {
        List<Payment> releasedPayments = paymentRepository.findByStatus("RELEASED");
        return releasedPayments.stream()
                .mapToDouble(Payment::getAmount)
                .sum();
    }

    // Calcular total de fondos reembolsados
    public Double calculateTotalRefunded() {
        List<Payment> refundedPayments = paymentRepository.findByStatus("REFUNDED");
        return refundedPayments.stream()
                .mapToDouble(Payment::getAmount)
                .sum();
    }

    // Limpiar pagos pendientes expirados
    public List<Payment> cleanupExpiredPayments() {
        LocalDateTime expirationTime = LocalDateTime.now().minusHours(24); // 24 horas
        List<Payment> expiredPayments = paymentRepository.findExpiredPendingPayments(expirationTime);
        
        // Cancelar pagos expirados
        for (Payment payment : expiredPayments) {
            payment.setStatus("CANCELLED");
            payment.setUpdatedAt(LocalDateTime.now());
            paymentRepository.save(payment);
        }
        
        return expiredPayments;
    }
}