package com.example.prm_project.utils.payment.vnpay;

import com.google.gson.annotations.SerializedName;

public class VNPayCallbackData {
    @SerializedName("vnp_Amount")
    private String vnpAmount;
    
    @SerializedName("vnp_BankCode")
    private String vnpBankCode;
    
    @SerializedName("vnp_BankTranNo")
    private String vnpBankTranNo;
    
    @SerializedName("vnp_CardType")
    private String vnpCardType;
    
    @SerializedName("vnp_OrderInfo")
    private String vnpOrderInfo;
    
    @SerializedName("vnp_PayDate")
    private String vnpPayDate;
    
    @SerializedName("vnp_ResponseCode")
    private String vnpResponseCode;
    
    @SerializedName("vnp_TmnCode")
    private String vnpTmnCode;
    
    @SerializedName("vnp_TransactionNo")
    private String vnpTransactionNo;
    
    @SerializedName("vnp_TransactionStatus")
    private String vnpTransactionStatus;
    
    @SerializedName("vnp_TxnRef")
    private String vnpTxnRef;
    
    @SerializedName("vnp_SecureHash")
    private String vnpSecureHash;
    
    // Getters
    public String getVnpAmount() { return vnpAmount; }
    public String getVnpBankCode() { return vnpBankCode; }
    public String getVnpBankTranNo() { return vnpBankTranNo; }
    public String getVnpCardType() { return vnpCardType; }
    public String getVnpOrderInfo() { return vnpOrderInfo; }
    public String getVnpPayDate() { return vnpPayDate; }
    public String getVnpResponseCode() { return vnpResponseCode; }
    public String getVnpTmnCode() { return vnpTmnCode; }
    public String getVnpTransactionNo() { return vnpTransactionNo; }
    public String getVnpTransactionStatus() { return vnpTransactionStatus; }
    public String getVnpTxnRef() { return vnpTxnRef; }
    public String getVnpSecureHash() { return vnpSecureHash; }
    
    // Setters
    public void setVnpAmount(String vnpAmount) { this.vnpAmount = vnpAmount; }
    public void setVnpBankCode(String vnpBankCode) { this.vnpBankCode = vnpBankCode; }
    public void setVnpBankTranNo(String vnpBankTranNo) { this.vnpBankTranNo = vnpBankTranNo; }
    public void setVnpCardType(String vnpCardType) { this.vnpCardType = vnpCardType; }
    public void setVnpOrderInfo(String vnpOrderInfo) { this.vnpOrderInfo = vnpOrderInfo; }
    public void setVnpPayDate(String vnpPayDate) { this.vnpPayDate = vnpPayDate; }
    public void setVnpResponseCode(String vnpResponseCode) { this.vnpResponseCode = vnpResponseCode; }
    public void setVnpTmnCode(String vnpTmnCode) { this.vnpTmnCode = vnpTmnCode; }
    public void setVnpTransactionNo(String vnpTransactionNo) { this.vnpTransactionNo = vnpTransactionNo; }
    public void setVnpTransactionStatus(String vnpTransactionStatus) { this.vnpTransactionStatus = vnpTransactionStatus; }
    public void setVnpTxnRef(String vnpTxnRef) { this.vnpTxnRef = vnpTxnRef; }
    public void setVnpSecureHash(String vnpSecureHash) { this.vnpSecureHash = vnpSecureHash; }
    
    // Helper methods
    public boolean isPaymentSuccessful() {
        return "00".equals(vnpResponseCode) && "00".equals(vnpTransactionStatus);
    }
    
    public double getAmountInUSD() {
        try {
            // VNPay returns amount in VND cents, convert to USD
            long vndAmount = Long.parseLong(vnpAmount);
            // Assuming 1 USD = 24,000 VND (you should use real exchange rate)
            return (double) vndAmount / 100 / 24000;
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
} 