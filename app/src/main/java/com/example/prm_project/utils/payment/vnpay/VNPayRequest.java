package com.example.prm_project.utils.payment.vnpay;

import com.example.prm_project.utils.payment.PaymentRequest;
import com.google.gson.annotations.SerializedName;

public class VNPayRequest extends PaymentRequest {
    @SerializedName("orderInfo")
    private String orderInfo;
    
    @SerializedName("orderType")
    private String orderType;
    
    @SerializedName("locale")
    private String locale;
    
    @SerializedName("returnUrl")
    private String returnUrl;
    
    @SerializedName("ipAddr")
    private String ipAddr;

    public VNPayRequest(double amount, String orderId, String description) {
        super(amount, "USD", orderId, description); // API expects USD amount
        this.orderInfo = description;
        this.orderType = "billpayment";
        this.locale = "vn";
        this.returnUrl = "mobilehomeservice://payment/result"; // Deep link for mobile app
        this.ipAddr = "127.0.0.1"; // Will be set by backend
    }

    // Getters
    public String getOrderInfo() { return orderInfo; }
    public String getOrderType() { return orderType; }
    public String getLocale() { return locale; }
    public String getReturnUrl() { return returnUrl; }
    public String getIpAddr() { return ipAddr; }

    // Setters
    public void setOrderInfo(String orderInfo) { this.orderInfo = orderInfo; }
    public void setOrderType(String orderType) { this.orderType = orderType; }
    public void setLocale(String locale) { this.locale = locale; }
    public void setReturnUrl(String returnUrl) { this.returnUrl = returnUrl; }
    public void setIpAddr(String ipAddr) { this.ipAddr = ipAddr; }
} 