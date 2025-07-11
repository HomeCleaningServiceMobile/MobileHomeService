package com.example.prm_project.data.model;

public enum PaymentMethod {
    CASH(1, "Cash"),
    CREDIT_CARD(2, "Credit Card"),
    DEBIT_CARD(3, "Debit Card"),
    BANK_TRANSFER(4, "Bank Transfer"),
    E_WALLET(5, "E-Wallet"),
    QR_CODE(6, "QR Code");

    private final int value;
    private final String displayName;

    PaymentMethod(int value, String displayName) {
        this.value = value;
        this.displayName = displayName;
    }

    public int getValue() {
        return value;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static PaymentMethod fromValue(int value) {
        for (PaymentMethod method : PaymentMethod.values()) {
            if (method.value == value) {
                return method;
            }
        }
        return CASH; // Default fallback
    }

    // Helper method to get all payment methods for UI selection
    public static PaymentMethod[] getAllMethods() {
        return PaymentMethod.values();
    }
} 