package com.touristchain.backend.roma.models;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class BookingModel {
    private Long id;
    private Long destinationId;
    private Long touristId;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private Integer numberOfGuests;
    private Double totalPrice;
    private String status; // PENDING, CONFIRMED, CANCELLED, COMPLETED
    private String currency = "USD";
    
    // Datos de Blockchain
    private String contractAddress;
    private String transactionHash;
    
    // Fechas de auditoría
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime confirmedAt;
    private LocalDateTime cancelledAt;
    
    // Constructor vacío (necesario para Spring)
    public BookingModel() {
    }
    
    // Constructor para crear nueva reserva
    public BookingModel(Long destinationId, Long touristId, LocalDate checkInDate, 
                       LocalDate checkOutDate, Integer numberOfGuests, Double totalPrice) {
        this.destinationId = destinationId;
        this.touristId = touristId;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.numberOfGuests = numberOfGuests;
        this.totalPrice = totalPrice;
        this.status = "PENDING"; // Estado inicial
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    // GETTERS Y SETTERS (métodos para acceder a los datos)
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getDestinationId() {
        return destinationId;
    }
    
    public void setDestinationId(Long destinationId) {
        this.destinationId = destinationId;
    }
    
    public Long getTouristId() {
        return touristId;
    }
    
    public void setTouristId(Long touristId) {
        this.touristId = touristId;
    }
    
    public LocalDate getCheckInDate() {
        return checkInDate;
    }
    
    public void setCheckInDate(LocalDate checkInDate) {
        this.checkInDate = checkInDate;
    }
    
    public LocalDate getCheckOutDate() {
        return checkOutDate;
    }
    
    public void setCheckOutDate(LocalDate checkOutDate) {
        this.checkOutDate = checkOutDate;
    }
    
    public Integer getNumberOfGuests() {
        return numberOfGuests;
    }
    
    public void setNumberOfGuests(Integer numberOfGuests) {
        this.numberOfGuests = numberOfGuests;
    }
    
    public Double getTotalPrice() {
        return totalPrice;
    }
    
    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getCurrency() {
        return currency;
    }
    
    public void setCurrency(String currency) {
        this.currency = currency;
    }
    
    public String getContractAddress() {
        return contractAddress;
    }
    
    public void setContractAddress(String contractAddress) {
        this.contractAddress = contractAddress;
    }
    
    public String getTransactionHash() {
        return transactionHash;
    }
    
    public void setTransactionHash(String transactionHash) {
        this.transactionHash = transactionHash;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public LocalDateTime getConfirmedAt() {
        return confirmedAt;
    }
    
    public void setConfirmedAt(LocalDateTime confirmedAt) {
        this.confirmedAt = confirmedAt;
    }
    
    public LocalDateTime getCancelledAt() {
        return cancelledAt;
    }
    
    public void setCancelledAt(LocalDateTime cancelledAt) {
        this.cancelledAt = cancelledAt;
    }
    
    // MÉTODOS DE UTILIDAD (lógica de negocio)
    
    // Verificar si la reserva está pendiente
    public boolean isPending() {
        return "PENDING".equals(this.status);
    }
    
    // Verificar si la reserva está confirmada
    public boolean isConfirmed() {
        return "CONFIRMED".equals(this.status);
    }
    
    // Verificar si la reserva está cancelada
    public boolean isCancelled() {
        return "CANCELLED".equals(this.status);
    }
    
    // Verificar si la reserva está completada
    public boolean isCompleted() {
        return "COMPLETED".equals(this.status);
    }
    
    // Calcular número de noches de estadía
    public long calculateNights() {
        return checkInDate.until(checkOutDate).getDays();
    }
    
    // Verificar si la reserva está activa (no cancelada)
    public boolean isActive() {
        return !isCancelled();
    }
}