package com.example.prm_project.ui.view.payment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import com.example.prm_project.R;
import com.example.prm_project.ui.view.MainActivity;
import com.example.prm_project.data.remote.RetrofitClient;
import com.example.prm_project.data.model.PaymentConfirmationResponse;
import com.example.prm_project.utils.payment.vnpay.VNPayCallbackData;
import com.example.prm_project.utils.SessionManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentCallbackActivity extends AppCompatActivity {
    private static final String TAG = "PaymentCallbackActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Handle the deep link intent
        Intent intent = getIntent();
        Uri data = intent.getData();
        
        if (data != null) {
            Log.d(TAG, "Received deep link: " + data.toString());
            handlePaymentCallback(data);
        } else {
            Log.e(TAG, "No deep link data received");
            navigateToFailure();
        }
    }
    
    private void handlePaymentCallback(Uri data) {
        try {
            // Extract VNPay parameters
            String responseCode = data.getQueryParameter("vnp_ResponseCode");
            String transactionStatus = data.getQueryParameter("vnp_TransactionStatus");
            String txnRef = data.getQueryParameter("vnp_TxnRef");
            String amount = data.getQueryParameter("vnp_Amount");
            String transactionNo = data.getQueryParameter("vnp_TransactionNo");
            String bankCode = data.getQueryParameter("vnp_BankCode");
            String payDate = data.getQueryParameter("vnp_PayDate");
            String secureHash = data.getQueryParameter("vnp_SecureHash");
            
            Log.d(TAG, "Payment callback - Response Code: " + responseCode + ", Transaction Status: " + transactionStatus);
            
            // Check if payment was successful
            boolean isSuccess = "00".equals(responseCode) && "00".equals(transactionStatus);
            
            if (isSuccess) {
                // Create VNPayCallbackData and confirm payment with backend
                VNPayCallbackData callbackData = new VNPayCallbackData();
                callbackData.setVnpResponseCode(responseCode);
                callbackData.setVnpTransactionStatus(transactionStatus);
                callbackData.setVnpTxnRef(txnRef);
                callbackData.setVnpAmount(amount);
                callbackData.setVnpTransactionNo(transactionNo);
                callbackData.setVnpBankCode(bankCode);
                callbackData.setVnpPayDate(payDate);
                callbackData.setVnpSecureHash(secureHash);
                
                confirmPaymentWithBackend(callbackData);
            } else {
                navigateToFailure();
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Error processing payment callback", e);
            navigateToFailure();
        }
    }
    
    private void confirmPaymentWithBackend(VNPayCallbackData callbackData) {
        // Get customer ID from session
        SessionManager sessionManager = new SessionManager(this);
        int customerId = sessionManager.getCurrentCustomerId();
        
        if (customerId == -1) {
            Log.e(TAG, "Customer not found - cannot confirm payment");
            navigateToFailure();
            return;
        }
        
        // For now, use a placeholder booking ID - in real implementation,
        // you would store the booking ID when starting payment
        int bookingId = 1; // This should come from the payment initiation
        
        Call<PaymentConfirmationResponse> call = 
            RetrofitClient.getPaymentApiService()
                .confirmVNPayPayment(customerId, bookingId, callbackData);

        call.enqueue(new Callback<PaymentConfirmationResponse>() {
            @Override
            public void onResponse(Call<PaymentConfirmationResponse> call, Response<PaymentConfirmationResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    PaymentConfirmationResponse confirmationResponse = response.body();
                    
                    if (confirmationResponse.isSucceeded()) {
                        Log.d(TAG, "Payment confirmed successfully");
                        navigateToSuccess();
                    } else {
                        Log.e(TAG, "Payment confirmation failed");
                        navigateToFailure();
                    }
                } else {
                    Log.e(TAG, "Failed to confirm payment - HTTP " + response.code());
                    navigateToFailure();
                }
            }

            @Override
            public void onFailure(Call<PaymentConfirmationResponse> call, Throwable t) {
                Log.e(TAG, "Network error during payment confirmation", t);
                navigateToFailure();
            }
        });
    }
    
    private void navigateToSuccess() {
        Log.d(TAG, "Navigating to success screen");
        
        // Create result intent for any waiting activities
        Intent resultIntent = new Intent();
        resultIntent.putExtra("payment_success", true);
        resultIntent.putExtra("payment_confirmed", true);
        setResult(RESULT_OK, resultIntent);
        
        // Navigate to main activity with success flag
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("navigate_to", "payment_success");
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
    
    private void navigateToFailure() {
        Log.d(TAG, "Navigating to failure screen");
        
        // Create result intent for any waiting activities
        Intent resultIntent = new Intent();
        resultIntent.putExtra("payment_success", false);
        resultIntent.putExtra("payment_confirmed", false);
        setResult(RESULT_CANCELED, resultIntent);
        
        // Navigate to main activity with failure flag
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("navigate_to", "payment_failure");
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
} 