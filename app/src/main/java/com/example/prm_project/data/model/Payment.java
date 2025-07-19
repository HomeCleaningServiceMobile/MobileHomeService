package com.example.prm_project.data.model;

import com.google.gson.annotations.SerializedName;

public class Payment {
    @SerializedName("id")
    private int id;
    
    @SerializedName("amount")
    private double amount;
    
    @SerializedName("paymentMethod")
    private int paymentMethod;
    
    @SerializedName("status")
    private int status;
    
    @SerializedName("paidAt")
    private String paidAt;

    // Default constructor
    public Payment() {}

    // Constructor
    public Payment(int id, double amount, int paymentMethod, int status, String paidAt) {
        this.id = id;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.status = status;
        this.paidAt = paidAt;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public int getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(int paymentMethod) { this.paymentMethod = paymentMethod; }

    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }

    public String getPaidAt() { return paidAt; }
    public void setPaidAt(String paidAt) { this.paidAt = paidAt; }

    // Helper method to get payment method display text
    public String getPaymentMethodDisplayText() {
        return PaymentMethod.fromValue(paymentMethod).getDisplayName();
    }
} 