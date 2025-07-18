package com.example.prm_project.data.model;

import com.google.gson.annotations.SerializedName;
import java.util.Map;

public class PaymentConfirmationResponse {
    @SerializedName("isSucceeded")
    private boolean isSucceeded;
    
    @SerializedName("timestamp")
    private String timestamp;
    
    @SerializedName("messages")
    private Map<String, String[]> messages;
    
    @SerializedName("data")
    private PaymentConfirmationData data;
    
    public static class PaymentConfirmationData {
        @SerializedName("isConfirmed")
        private boolean isConfirmed;
        
        @SerializedName("transactionId")
        private String transactionId;
        
        @SerializedName("amountDeducted")
        private double amountDeducted;
        
        @SerializedName("remainingBalance")
        private double remainingBalance;
        
        @SerializedName("bookingId")
        private int bookingId;
        
        @SerializedName("bookingStatus")
        private String bookingStatus;
        
        @SerializedName("paymentProvider")
        private String paymentProvider;
        
        @SerializedName("confirmedAt")
        private String confirmedAt;
        
        // Getters
        public boolean isConfirmed() { return isConfirmed; }
        public String getTransactionId() { return transactionId; }
        public double getAmountDeducted() { return amountDeducted; }
        public double getRemainingBalance() { return remainingBalance; }
        public int getBookingId() { return bookingId; }
        public String getBookingStatus() { return bookingStatus; }
        public String getPaymentProvider() { return paymentProvider; }
        public String getConfirmedAt() { return confirmedAt; }
        
        // Setters
        public void setConfirmed(boolean confirmed) { isConfirmed = confirmed; }
        public void setTransactionId(String transactionId) { this.transactionId = transactionId; }
        public void setAmountDeducted(double amountDeducted) { this.amountDeducted = amountDeducted; }
        public void setRemainingBalance(double remainingBalance) { this.remainingBalance = remainingBalance; }
        public void setBookingId(int bookingId) { this.bookingId = bookingId; }
        public void setBookingStatus(String bookingStatus) { this.bookingStatus = bookingStatus; }
        public void setPaymentProvider(String paymentProvider) { this.paymentProvider = paymentProvider; }
        public void setConfirmedAt(String confirmedAt) { this.confirmedAt = confirmedAt; }
    }
    
    // Getters
    public boolean isSucceeded() { return isSucceeded; }
    public String getTimestamp() { return timestamp; }
    public Map<String, String[]> getMessages() { return messages; }
    public PaymentConfirmationData getData() { return data; }
    
    // Setters
    public void setSucceeded(boolean succeeded) { isSucceeded = succeeded; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
    public void setMessages(Map<String, String[]> messages) { this.messages = messages; }
    public void setData(PaymentConfirmationData data) { this.data = data; }
} 