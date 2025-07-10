package com.example.prm_project.data.model;

import com.google.gson.annotations.SerializedName;

public class TokenResponse {
    
    @SerializedName("token")
    private String token;
    
    @SerializedName("expiresAt")
    private String expiresAt;
    
    // Constructors
    public TokenResponse() {}
    
    public TokenResponse(String token, String expiresAt) {
        this.token = token;
        this.expiresAt = expiresAt;
    }
    
    // Getters and Setters
    public String getToken() {
        return token;
    }
    
    public void setToken(String token) {
        this.token = token;
    }
    
    public String getExpiresAt() {
        return expiresAt;
    }
    
    public void setExpiresAt(String expiresAt) {
        this.expiresAt = expiresAt;
    }
} 