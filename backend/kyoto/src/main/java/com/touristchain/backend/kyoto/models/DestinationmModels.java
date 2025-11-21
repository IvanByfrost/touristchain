package com.touristchain.backend.kyoto.models;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "destinations")
@Data
public class DestinationmModels {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    private String description;
    private String country;
    private String city;
    private String category;
    private Double pricePerDay;
    private String currency = "USD";
    
    @ElementCollection
    private java.util.List<String> images;
    
    @ElementCollection
    private java.util.List<String> tags;
    
    private Double rating = 0.0;
    private Integer totalReviews = 0;
    private String contractAddress;
    private String providerWallet;
    private Boolean active = true;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}