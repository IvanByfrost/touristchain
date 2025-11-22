package com.touristchain.backend.newyork.controllers;

import com.touristchain.backend.newyork.models.PaymentModels.Payment;
import com.touristchain.backend.newyork.services.PaymentServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/newyork/payments")
@CrossOrigin(origins = "*")
public class PaymentController {

    @Autowired
    private PaymentServices paymentServices;

    // Crear nuevo pago escrow
    @PostMapping("/escrow")
    public ResponseEntity<?> createEscrowPayment(@RequestBody CreateEscrowRequest request) {
        try {
            Payment payment = paymentServices.createEscrowPayment(
                request.getBookingId(),
                request.getTouristWallet(),
                request.getProviderWallet(),
                request.getAmount()
            );
            return ResponseEntity.ok(payment);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Obtener todos los pagos
    @GetMapping
    public ResponseEntity<List<Payment>> getAllPayments() {
        List<Payment> payments = paymentServices.findByStatus("PENDING");
        return ResponseEntity.ok(payments);
    }

    // Obtener pago por ID
    @GetMapping("/{id}")
    public ResponseEntity<Payment> getPaymentById(@PathVariable Long id) {
        Optional<Payment> payment = paymentServices.findById(id);
        return payment.map(ResponseEntity::ok)
                     .orElse(ResponseEntity.notFound().build());
    }

    // Obtener pagos por bookingId
    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<List<Payment>> getPaymentsByBooking(@PathVariable Long bookingId) {
        List<Payment> payments = paymentServices.findByBookingId(bookingId);
        return ResponseEntity.ok(payments);
    }

    // Obtener pagos por turista
    @GetMapping("/tourist/{walletAddress}")
    public ResponseEntity<List<Payment>> getPaymentsByTourist(@PathVariable String walletAddress) {
        List<Payment> payments = paymentServices.findByTouristWallet(walletAddress);
        return ResponseEntity.ok(payments);
    }

    // Obtener pagos por proveedor
    @GetMapping("/provider/{walletAddress}")
    public ResponseEntity<List<Payment>> getPaymentsByProvider(@PathVariable String walletAddress) {
        List<Payment> payments = paymentServices.findByProviderWallet(walletAddress);
        return ResponseEntity.ok(payments);
    }

    // Obtener pagos por estado
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Payment>> getPaymentsByStatus(@PathVariable String status) {
        List<Payment> payments = paymentServices.findByStatus(status);
        return ResponseEntity.ok(payments);
    }

    // Obtener pago especifico por bookingId
    @GetMapping("/booking/{bookingId}/payment")
    public ResponseEntity<Payment> getPaymentForBooking(@PathVariable Long bookingId) {
        Optional<Payment> payment = paymentServices.getPaymentByBookingId(bookingId);
        return payment.map(ResponseEntity::ok)
                     .orElse(ResponseEntity.notFound().build());
    }

    // Marcar pago como depositado
    @PatchMapping("/{id}/deposit")
    public ResponseEntity<?> markAsDeposited(@PathVariable Long id, @RequestBody DepositRequest request) {
        try {
            Optional<Payment> payment = paymentServices.markAsDeposited(
                id, 
                request.getContractAddress(), 
                request.getDepositTxHash()
            );
            return payment.map(ResponseEntity::ok)
                         .orElse(ResponseEntity.notFound().build());
        } catch (IllegalStateException | IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Liberar fondos al proveedor
    @PatchMapping("/{id}/release")
    public ResponseEntity<?> releaseToProvider(@PathVariable Long id, @RequestBody ReleaseRequest request) {
        try {
            Optional<Payment> payment = paymentServices.releaseToProvider(id, request.getReleaseTxHash());
            return payment.map(ResponseEntity::ok)
                         .orElse(ResponseEntity.notFound().build());
        } catch (IllegalStateException | IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Reembolsar fondos al turista
    @PatchMapping("/{id}/refund")
    public ResponseEntity<?> refundToTourist(@PathVariable Long id, @RequestBody RefundRequest request) {
        try {
            Optional<Payment> payment = paymentServices.refundToTourist(id, request.getRefundTxHash());
            return payment.map(ResponseEntity::ok)
                         .orElse(ResponseEntity.notFound().build());
        } catch (IllegalStateException | IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Cancelar pago
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<?> cancelPayment(@PathVariable Long id) {
        try {
            Optional<Payment> payment = paymentServices.cancelPayment(id);
            return payment.map(ResponseEntity::ok)
                         .orElse(ResponseEntity.notFound().build());
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Obtener pagos escrow activos
    @GetMapping("/escrow/active")
    public ResponseEntity<List<Payment>> getActiveEscrowPayments() {
        List<Payment> payments = paymentServices.getActiveEscrowPayments();
        return ResponseEntity.ok(payments);
    }

    // Obtener pagos listos para liberar
    @GetMapping("/ready-for-release")
    public ResponseEntity<List<Payment>> getPaymentsReadyForRelease() {
        List<Payment> payments = paymentServices.getPaymentsReadyForRelease();
        return ResponseEntity.ok(payments);
    }

    // Obtener estadisticas
    @GetMapping("/stats")
    public ResponseEntity<?> getStats() {
        Object stats = paymentServices.getPaymentStatistics();
        return ResponseEntity.ok(stats);
    }

    // Obtener total de fondos liberados
    @GetMapping("/revenue/released")
    public ResponseEntity<Double> getTotalReleased() {
        Double total = paymentServices.calculateTotalReleased();
        return ResponseEntity.ok(total);
    }

    // Obtener total de fondos reembolsados
    @GetMapping("/revenue/refunded")
    public ResponseEntity<Double> getTotalRefunded() {
        Double total = paymentServices.calculateTotalRefunded();
        return ResponseEntity.ok(total);
    }

    // Limpiar pagos expirados
    @PostMapping("/cleanup/expired")
    public ResponseEntity<List<Payment>> cleanupExpiredPayments() {
        List<Payment> cleanedPayments = paymentServices.cleanupExpiredPayments();
        return ResponseEntity.ok(cleanedPayments);
    }

    // Clases internas para requests
    public static class CreateEscrowRequest {
        private Long bookingId;
        private String touristWallet;
        private String providerWallet;
        private Double amount;

        public Long getBookingId() { return bookingId; }
        public void setBookingId(Long bookingId) { this.bookingId = bookingId; }

        public String getTouristWallet() { return touristWallet; }
        public void setTouristWallet(String touristWallet) { this.touristWallet = touristWallet; }

        public String getProviderWallet() { return providerWallet; }
        public void setProviderWallet(String providerWallet) { this.providerWallet = providerWallet; }

        public Double getAmount() { return amount; }
        public void setAmount(Double amount) { this.amount = amount; }
    }

    public static class DepositRequest {
        private String contractAddress;
        private String depositTxHash;

        public String getContractAddress() { return contractAddress; }
        public void setContractAddress(String contractAddress) { this.contractAddress = contractAddress; }

        public String getDepositTxHash() { return depositTxHash; }
        public void setDepositTxHash(String depositTxHash) { this.depositTxHash = depositTxHash; }
    }

    public static class ReleaseRequest {
        private String releaseTxHash;

        public String getReleaseTxHash() { return releaseTxHash; }
        public void setReleaseTxHash(String releaseTxHash) { this.releaseTxHash = releaseTxHash; }
    }

    public static class RefundRequest {
        private String refundTxHash;

        public String getRefundTxHash() { return refundTxHash; }
        public void setRefundTxHash(String refundTxHash) { this.refundTxHash = refundTxHash; }
    }
}