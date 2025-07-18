package com.example.prm_project.utils.payment.stripe;

import com.google.gson.annotations.SerializedName;

public class StripeResponse {
    @SerializedName("client_secret")
    private String clientSecret;
    
    @SerializedName("success")
    private boolean success;
    
    @SerializedName("sessionId")
    private String sessionId;
    
    @SerializedName("paymentUrl")
    private String paymentUrl;
    
    @SerializedName("message")
    private String message;
    
    @SerializedName("provider")
    private String provider;
    
    // Getters
    public String getClientSecret() { return clientSecret; }
    public boolean isSuccess() { return success; }
    public String getSessionId() { return sessionId; }
    public String getPaymentUrl() { return paymentUrl; }
    public String getMessage() { return message; }
    public String getProvider() { return provider; }
    
    // Setters
    public void setClientSecret(String clientSecret) { this.clientSecret = clientSecret; }
    public void setSuccess(boolean success) { this.success = success; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    public void setPaymentUrl(String paymentUrl) { this.paymentUrl = paymentUrl; }
    public void setMessage(String message) { this.message = message; }
    public void setProvider(String provider) { this.provider = provider; }
} 