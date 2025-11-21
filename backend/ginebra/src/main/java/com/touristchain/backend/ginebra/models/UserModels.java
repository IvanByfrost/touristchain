package com.touristchain.backend.ginebra.models;

import java.time.LocalDateTime;

public class User {
    private Long id;
    private String username;
    private String email;
    private String walletAddress;
    private String userType; // TOURIST, PROVIDER, ADMIN
    private Boolean verified = false;
    private Boolean active = true;
    private String did; // Identificador descentralizado
    private String contractAddress;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastLogin;
    
    // Constructor vacío
    public User() {}
    
    // Constructor básico
    public User(String username, String email, String walletAddress) {
        this.username = username;
        this.email = email;
        this.walletAddress = walletAddress;
        this.userType = "TOURIST";
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }
    
    // GETTERS AND SETTERS
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getWalletAddress() { return walletAddress; }
    public void setWalletAddress(String walletAddress) { this.walletAddress = walletAddress; }
    
    public String getUserType() { return userType; }
    public void setUserType(String userType) { this.userType = userType; }
    
    public Boolean getVerified() { return verified; }
    public void setVerified(Boolean verified) { this.verified = verified; }
    
    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
    
    public String getDid() { return did; }
    public void setDid(String did) { this.did = did; }
    
    public String getContractAddress() { return contractAddress; }
    public void setContractAddress(String contractAddress) { this.contractAddress = contractAddress; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public LocalDateTime getLastLogin() { return lastLogin; }
    public void setLastLogin(LocalDateTime lastLogin) { this.lastLogin = lastLogin; }
    
    // Métodos de utilidad
    public boolean isTourist() { return "TOURIST".equals(userType); }
    public boolean isProvider() { return "PROVIDER".equals(userType); }
    public boolean isAdmin() { return "ADMIN".equals(userType); }
}   