package com.example.prm_project.data.model;

import com.google.gson.annotations.SerializedName;

public class ForgotPasswordRequest {
    
    @SerializedName("email")
    private String email;
    
    // Constructors
    public ForgotPasswordRequest() {}
    
    public ForgotPasswordRequest(String email) {
        this.email = email;
    }
    
    // Getters and Setters
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
} 