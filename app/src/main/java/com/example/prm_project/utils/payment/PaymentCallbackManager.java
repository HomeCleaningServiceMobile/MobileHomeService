package com.example.prm_project.utils.payment;

public class PaymentCallbackManager {
    private static PaymentCallbackManager instance;
    private PaymentProcessor.PaymentCallback currentCallback;

    private PaymentCallbackManager() {}

    public static synchronized PaymentCallbackManager getInstance() {
        if (instance == null) {
            instance = new PaymentCallbackManager();
        }
        return instance;
    }

    public void setCallback(PaymentProcessor.PaymentCallback callback) {
        this.currentCallback = callback;
    }

    public PaymentProcessor.PaymentCallback getCallback() {
        return currentCallback;
    }

    public void clearCallback() {
        this.currentCallback = null;
    }
} 