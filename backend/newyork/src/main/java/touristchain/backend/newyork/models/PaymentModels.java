package com.touristchain.backend.newyork.models;

import java.time.LocalDateTime;

public class PaymentModels {
    
    public static class Payment {
        private Long id;
        private Long bookingId;
        private String touristWallet;
        private String providerWallet;
        private Double amount;
        private String currency = "USD";
        private String status; // PENDING, DEPOSITED, RELEASED, REFUNDED, CANCELLED
        private String paymentType; // ESCROW, DIRECT, REFUND
        
        // Blockchain data
        private String contractAddress;
        private String depositTxHash;
        private String releaseTxHash;
        private String refundTxHash;
        private String smartContractId;
        
        // Timestamps
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private LocalDateTime depositedAt;
        private LocalDateTime releasedAt;
        private LocalDateTime refundedAt;
        
        // Constructor vacío
        public Payment() {
        }
        
        // Constructor para pago escrow
        public Payment(Long bookingId, String touristWallet, String providerWallet, Double amount) {
            this.bookingId = bookingId;
            this.touristWallet = touristWallet;
            this.providerWallet = providerWallet;
            this.amount = amount;
            this.status = "PENDING";
            this.paymentType = "ESCROW";
            this.createdAt = LocalDateTime.now();
            this.updatedAt = LocalDateTime.now();
        }
        
        // GETTERS AND SETTERS
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        
        public Long getBookingId() { return bookingId; }
        public void setBookingId(Long bookingId) { this.bookingId = bookingId; }
        
        public String getTouristWallet() { return touristWallet; }
        public void setTouristWallet(String touristWallet) { this.touristWallet = touristWallet; }
        
        public String getProviderWallet() { return providerWallet; }
        public void setProviderWallet(String providerWallet) { this.providerWallet = providerWallet; }
        
        public Double getAmount() { return amount; }
        public void setAmount(Double amount) { this.amount = amount; }
        
        public String getCurrency() { return currency; }
        public void setCurrency(String currency) { this.currency = currency; }
        
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        
        public String getPaymentType() { return paymentType; }
        public void setPaymentType(String paymentType) { this.paymentType = paymentType; }
        
        public String getContractAddress() { return contractAddress; }
        public void setContractAddress(String contractAddress) { this.contractAddress = contractAddress; }
        
        public String getDepositTxHash() { return depositTxHash; }
        public void setDepositTxHash(String depositTxHash) { this.depositTxHash = depositTxHash; }
        
        public String getReleaseTxHash() { return releaseTxHash; }
        public void setReleaseTxHash(String releaseTxHash) { this.releaseTxHash = releaseTxHash; }
        
        public String getRefundTxHash() { return refundTxHash; }
        public void setRefundTxHash(String refundTxHash) { this.refundTxHash = refundTxHash; }
        
        public String getSmartContractId() { return smartContractId; }
        public void setSmartContractId(String smartContractId) { this.smartContractId = smartContractId; }
        
        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
        
        public LocalDateTime getUpdatedAt() { return updatedAt; }
        public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
        
        public LocalDateTime getDepositedAt() { return depositedAt; }
        public void setDepositedAt(LocalDateTime depositedAt) { this.depositedAt = depositedAt; }
        
        public LocalDateTime getReleasedAt() { return releasedAt; }
        public void setReleasedAt(LocalDateTime releasedAt) { this.releasedAt = releasedAt; }
        
        public LocalDateTime getRefundedAt() { return refundedAt; }
        public void setRefundedAt(LocalDateTime refundedAt) { this.refundedAt = refundedAt; }
        
        // Métodos de utilidad
        public boolean isPending() { return "PENDING".equals(status); }
        public boolean isDeposited() { return "DEPOSITED".equals(status); }
        public boolean isReleased() { return "RELEASED".equals(status); }
        public boolean isRefunded() { return "REFUNDED".equals(status); }
        public boolean isEscrow() { return "ESCROW".equals(paymentType); }
    }
    
    public static class EscrowContract {
        private String contractAddress;
        private String touristWallet;
        private String providerWallet;
        private Double amount;
        private String currency;
        private LocalDateTime expirationDate;
        private String contractState; // ACTIVE, RELEASED, REFUNDED, EXPIRED
        
        public EscrowContract() {
        }
        
        public EscrowContract(String contractAddress, String touristWallet, String providerWallet, Double amount) {
            this.contractAddress = contractAddress;
            this.touristWallet = touristWallet;
            this.providerWallet = providerWallet;
            this.amount = amount;
            this.currency = "USD";
            this.contractState = "ACTIVE";
        }
        
        // GETTERS AND SETTERS
        public String getContractAddress() { return contractAddress; }
        public void setContractAddress(String contractAddress) { this.contractAddress = contractAddress; }
        
        public String getTouristWallet() { return touristWallet; }
        public void setTouristWallet(String touristWallet) { this.touristWallet = touristWallet; }
        
        public String getProviderWallet() { return providerWallet; }
        public void setProviderWallet(String providerWallet) { this.providerWallet = providerWallet; }
        
        public Double getAmount() { return amount; }
        public void setAmount(Double amount) { this.amount = amount; }
        
        public String getCurrency() { return currency; }
        public void setCurrency(String currency) { this.currency = currency; }
        
        public LocalDateTime getExpirationDate() { return expirationDate; }
        public void setExpirationDate(LocalDateTime expirationDate) { this.expirationDate = expirationDate; }
        
        public String getContractState() { return contractState; }
        public void setContractState(String contractState) { this.contractState = contractState; }
    }
    
    public static class Transaction {
        private String txHash;
        private String fromWallet;
        private String toWallet;
        private Double amount;
        private String currency;
        private String type; // DEPOSIT, RELEASE, REFUND
        private LocalDateTime timestamp;
        private Boolean confirmed = false;
        private Integer blockNumber;
        
        public Transaction() {
        }
        
        public Transaction(String txHash, String fromWallet, String toWallet, Double amount, String type) {
            this.txHash = txHash;
            this.fromWallet = fromWallet;
            this.toWallet = toWallet;
            this.amount = amount;
            this.type = type;
            this.currency = "USD";
            this.timestamp = LocalDateTime.now();
        }
        
        // GETTERS AND SETTERS
        public String getTxHash() { return txHash; }
        public void setTxHash(String txHash) { this.txHash = txHash; }
        
        public String getFromWallet() { return fromWallet; }
        public void setFromWallet(String fromWallet) { this.fromWallet = fromWallet; }
        
        public String getToWallet() { return toWallet; }
        public void setToWallet(String toWallet) { this.toWallet = toWallet; }
        
        public Double getAmount() { return amount; }
        public void setAmount(Double amount) { this.amount = amount; }
        
        public String getCurrency() { return currency; }
        public void setCurrency(String currency) { this.currency = currency; }
        
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        
        public LocalDateTime getTimestamp() { return timestamp; }
        public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
        
        public Boolean getConfirmed() { return confirmed; }
        public void setConfirmed(Boolean confirmed) { this.confirmed = confirmed; }
        
        public Integer getBlockNumber() { return blockNumber; }
        public void setBlockNumber(Integer blockNumber) { this.blockNumber = blockNumber; }
    }
}