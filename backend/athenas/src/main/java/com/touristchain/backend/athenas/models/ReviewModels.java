package main.java.com.touristchain.backend.athenas.models;

import java.time.LocalDateTime;

public class ReviewModels {
    
    public static class Review {
        private Long id;
        private Long bookingId;
        private Long destinationId;
        private Long touristId;
        private Long providerId;
        
        // Calificaciones
        private Integer rating; // 1-5
        private Integer cleanliness;
        private Integer service;
        private Integer location;
        private Integer value;
        
        // Comentarios
        private String title;
        private String comment;
        private String response; // Respuesta del proveedor
        
        // Blockchain data
        private String transactionHash;
        private Boolean verified = false;
        
        // Timestamps
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private LocalDateTime respondedAt;
        
        // Constructor vacío
        public Review() {
        }
        
        // Constructor básico
        public Review(Long bookingId, Long destinationId, Long touristId, Long providerId, Integer rating) {
            this.bookingId = bookingId;
            this.destinationId = destinationId;
            this.touristId = touristId;
            this.providerId = providerId;
            this.rating = rating;
            this.createdAt = LocalDateTime.now();
            this.updatedAt = LocalDateTime.now();
        }
        
        // GETTERS AND SETTERS
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        
        public Long getBookingId() { return bookingId; }
        public void setBookingId(Long bookingId) { this.bookingId = bookingId; }
        
        public Long getDestinationId() { return destinationId; }
        public void setDestinationId(Long destinationId) { this.destinationId = destinationId; }
        
        public Long getTouristId() { return touristId; }
        public void setTouristId(Long touristId) { this.touristId = touristId; }
        
        public Long getProviderId() { return providerId; }
        public void setProviderId(Long providerId) { this.providerId = providerId; }
        
        public Integer getRating() { return rating; }
        public void setRating(Integer rating) { this.rating = rating; }
        
        public Integer getCleanliness() { return cleanliness; }
        public void setCleanliness(Integer cleanliness) { this.cleanliness = cleanliness; }
        
        public Integer getService() { return service; }
        public void setService(Integer service) { this.service = service; }
        
        public Integer getLocation() { return location; }
        public void setLocation(Integer location) { this.location = location; }
        
        public Integer getValue() { return value; }
        public void setValue(Integer value) { this.value = value; }
        
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        
        public String getComment() { return comment; }
        public void setComment(String comment) { this.comment = comment; }
        
        public String getResponse() { return response; }
        public void setResponse(String response) { this.response = response; }
        
        public String getTransactionHash() { return transactionHash; }
        public void setTransactionHash(String transactionHash) { this.transactionHash = transactionHash; }
        
        public Boolean getVerified() { return verified; }
        public void setVerified(Boolean verified) { this.verified = verified; }
        
        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
        
        public LocalDateTime getUpdatedAt() { return updatedAt; }
        public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
        
        public LocalDateTime getRespondedAt() { return respondedAt; }
        public void setRespondedAt(LocalDateTime respondedAt) { this.respondedAt = respondedAt; }
        
        // Métodos de utilidad
        public boolean hasResponse() { return response != null && !response.trim().isEmpty(); }
        
        public Double calculateAverageRating() {
            int count = 1; // rating principal siempre existe
            double sum = rating;
            
            if (cleanliness != null) { sum += cleanliness; count++; }
            if (service != null) { sum += service; count++; }
            if (location != null) { sum += location; count++; }
            if (value != null) { sum += value; count++; }
            
            return sum / count;
        }
    }
    
    public static class ReputationScore {
        private Long providerId;
        private Double overallRating;
        private Integer totalReviews;
        private Integer fiveStar;
        private Integer fourStar;
        private Integer threeStar;
        private Integer twoStar;
        private Integer oneStar;
        private Double responseRate;
        private LocalDateTime lastUpdated;
        
        public ReputationScore() {
        }
        
        public ReputationScore(Long providerId) {
            this.providerId = providerId;
            this.overallRating = 0.0;
            this.totalReviews = 0;
            this.lastUpdated = LocalDateTime.now();
        }
        
        // GETTERS AND SETTERS
        public Long getProviderId() { return providerId; }
        public void setProviderId(Long providerId) { this.providerId = providerId; }
        
        public Double getOverallRating() { return overallRating; }
        public void setOverallRating(Double overallRating) { this.overallRating = overallRating; }
        
        public Integer getTotalReviews() { return totalReviews; }
        public void setTotalReviews(Integer totalReviews) { this.totalReviews = totalReviews; }
        
        public Integer getFiveStar() { return fiveStar; }
        public void setFiveStar(Integer fiveStar) { this.fiveStar = fiveStar; }
        
        public Integer getFourStar() { return fourStar; }
        public void setFourStar(Integer fourStar) { this.fourStar = fourStar; }
        
        public Integer getThreeStar() { return threeStar; }
        public void setThreeStar(Integer threeStar) { this.threeStar = threeStar; }
        
        public Integer getTwoStar() { return twoStar; }
        public void setTwoStar(Integer twoStar) { this.twoStar = twoStar; }
        
        public Integer getOneStar() { return oneStar; }
        public void setOneStar(Integer oneStar) { this.oneStar = oneStar; }
        
        public Double getResponseRate() { return responseRate; }
        public void setResponseRate(Double responseRate) { this.responseRate = responseRate; }
        
        public LocalDateTime getLastUpdated() { return lastUpdated; }
        public void setLastUpdated(LocalDateTime lastUpdated) { this.lastUpdated = lastUpdated; }
        
        // Métodos de utilidad
        public void incrementStarCount(Integer rating) {
            if (rating == 5) fiveStar = (fiveStar == null ? 1 : fiveStar + 1);
            else if (rating == 4) fourStar = (fourStar == null ? 1 : fourStar + 1);
            else if (rating == 3) threeStar = (threeStar == null ? 1 : threeStar + 1);
            else if (rating == 2) twoStar = (twoStar == null ? 1 : twoStar + 1);
            else if (rating == 1) oneStar = (oneStar == null ? 1 : oneStar + 1);
        }
        
        public void recalculate() {
            totalReviews = (fiveStar != null ? fiveStar : 0) +
                          (fourStar != null ? fourStar : 0) +
                          (threeStar != null ? threeStar : 0) +
                          (twoStar != null ? twoStar : 0) +
                          (oneStar != null ? oneStar : 0);
            
            if (totalReviews > 0) {
                double weightedSum = (fiveStar != null ? fiveStar * 5 : 0) +
                                    (fourStar != null ? fourStar * 4 : 0) +
                                    (threeStar != null ? threeStar * 3 : 0) +
                                    (twoStar != null ? twoStar * 2 : 0) +
                                    (oneStar != null ? oneStar * 1 : 0);
                overallRating = weightedSum / totalReviews;
            } else {
                overallRating = 0.0;
            }
        }
    }
    
    public static class DestinationRating {
        private Long destinationId;
        private Double overallRating;
        private Integer totalReviews;
        private Double cleanliness;
        private Double service;
        private Double location;
        private Double value;
        private LocalDateTime lastUpdated;
        
        public DestinationRating() {
        }
        
        public DestinationRating(Long destinationId) {
            this.destinationId = destinationId;
            this.overallRating = 0.0;
            this.totalReviews = 0;
            this.lastUpdated = LocalDateTime.now();
        }
        
        // GETTERS AND SETTERS
        public Long getDestinationId() { return destinationId; }
        public void setDestinationId(Long destinationId) { this.destinationId = destinationId; }
        
        public Double getOverallRating() { return overallRating; }
        public void setOverallRating(Double overallRating) { this.overallRating = overallRating; }
        
        public Integer getTotalReviews() { return totalReviews; }
        public void setTotalReviews(Integer totalReviews) { this.totalReviews = totalReviews; }
        
        public Double getCleanliness() { return cleanliness; }
        public void setCleanliness(Double cleanliness) { this.cleanliness = cleanliness; }
        
        public Double getService() { return service; }
        public void setService(Double service) { this.service = service; }
        
        public Double getLocation() { return location; }
        public void setLocation(Double location) { this.location = location; }
        
        public Double getValue() { return value; }
        public void setValue(Double value) { this.value = value; }
        
        public LocalDateTime getLastUpdated() { return lastUpdated; }
        public void setLastUpdated(LocalDateTime lastUpdated) { this.lastUpdated = lastUpdated; }
    }
}