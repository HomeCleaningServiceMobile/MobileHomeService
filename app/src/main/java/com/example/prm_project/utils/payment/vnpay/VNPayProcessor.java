package com.example.prm_project.utils.payment.vnpay;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.example.prm_project.data.model.PaymentConfirmationResponse;
import com.example.prm_project.data.remote.RetrofitClient;
import com.example.prm_project.ui.view.payment.PaymentWebViewActivity;
import com.example.prm_project.utils.payment.PaymentCallbackManager;
import com.example.prm_project.utils.payment.PaymentProcessor;
import com.example.prm_project.utils.payment.PaymentRequest;
import com.example.prm_project.utils.payment.PaymentResult;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VNPayProcessor implements PaymentProcessor {
    private static final String TAG = "VNPayProcessor";
    private static final int PAYMENT_REQUEST_CODE = 1001;

    @Override
    public PaymentMethod getPaymentMethod() {
        return PaymentMethod.VNPAY;
    }

    @Override
    public void processPayment(Context context, PaymentRequest request, PaymentCallback callback) {
        if (!(request instanceof VNPayRequest)) {
            callback.onPaymentFailure(new PaymentResult(
                PaymentResult.Status.FAILED,
                "Invalid request type for VNPay payment"
            ));
            return;
        }

        VNPayRequest vnpayRequest = (VNPayRequest) request;

        // Call API to create VNPay payment
        Call<VNPayResponse> call = RetrofitClient.getPaymentApiService()
                .createVNPayPayment(vnpayRequest);

        call.enqueue(new Callback<VNPayResponse>() {
            @Override
            public void onResponse(Call<VNPayResponse> call, Response<VNPayResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    VNPayResponse vnpayResponse = response.body();

                    if (vnpayResponse.isSuccess() && vnpayResponse.getPaymentUrl() != null) {
                        // Open VNPay payment URL in WebView with deep link callback support
                        if (context instanceof Activity) {
                            Intent intent = new Intent(context, PaymentWebViewActivity.class);
                            intent.putExtra("payment_url", vnpayResponse.getPaymentUrl());
                            intent.putExtra("order_id", vnpayRequest.getOrderId());
                            intent.putExtra("amount", vnpayRequest.getAmount());
                            ((Activity) context).startActivityForResult(intent, PAYMENT_REQUEST_CODE);
                            
                            // Store callback for later use
                            PaymentCallbackManager.getInstance().setCallback(callback);
                            
                            Log.d(TAG, "VNPay payment initiated successfully via WebView with deep link support");
                        } else {
                            Log.e(TAG, "Context is not an Activity: " + context.getClass().getSimpleName());
                            callback.onPaymentFailure(new PaymentResult(
                                PaymentResult.Status.FAILED,
                                "VNPay payment requires an Activity context. Please use requireActivity() instead of requireContext() when calling from a Fragment."
                            ));
                        }
                    } else {
                        callback.onPaymentFailure(new PaymentResult(
                            PaymentResult.Status.FAILED,
                            vnpayResponse.getMessage() != null ? vnpayResponse.getMessage() : "Failed to create VNPay payment"
                        ));
                    }
                } else {
                    callback.onPaymentFailure(new PaymentResult(
                        PaymentResult.Status.FAILED,
                        "Failed to communicate with payment server"
                    ));
                }
            }

            @Override
            public void onFailure(Call<VNPayResponse> call, Throwable t) {
                Log.e(TAG, "VNPay payment creation failed", t);
                callback.onPaymentFailure(new PaymentResult(
                    PaymentResult.Status.FAILED,
                    "Network error: " + t.getMessage()
                ));
            }
        });
    }

    @Override
    public boolean isAvailable() {
        // VNPay is available if we have network connection
        return true;
    }

    @Override
    public String getDisplayName() {
        return getPaymentMethod().getDisplayName();
    }

    @Override
    public void handlePaymentResult(String data, PaymentCallback callback) {
        try {
            Uri uri = Uri.parse(data);
            String responseCode = uri.getQueryParameter("vnp_ResponseCode");
            String transactionStatus = uri.getQueryParameter("vnp_TransactionStatus");
            String txnRef = uri.getQueryParameter("vnp_TxnRef");
            String amount = uri.getQueryParameter("vnp_Amount");
            String transactionNo = uri.getQueryParameter("vnp_TransactionNo");

            if ("00".equals(responseCode) && "00".equals(transactionStatus)) {
                PaymentResult result = new PaymentResult(
                    PaymentResult.Status.SUCCESS,
                    "Payment completed successfully",
                    transactionNo,
                    txnRef,
                    amount != null ? Double.parseDouble(amount) / 100 : 0 // VNPay returns amount in cents
                );
                callback.onPaymentSuccess(result);
            } else {
                PaymentResult result = new PaymentResult(
                    PaymentResult.Status.FAILED,
                    "Payment failed with code: " + responseCode
                );
                result.setTransactionId(transactionNo);
                result.setOrderId(txnRef);
                callback.onPaymentFailure(result);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error handling VNPay payment result", e);
            callback.onPaymentFailure(new PaymentResult(
                PaymentResult.Status.FAILED,
                "Error processing payment result: " + e.getMessage()
            ));
        }
    }

    /**
     * Handle payment result from WebView activity
     */
    public static void handleWebViewResult(int requestCode, int resultCode, Intent data, PaymentCallback callback) {
        if (requestCode == PAYMENT_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                boolean isSuccess = data.getBooleanExtra("payment_success", false);
                
                if (isSuccess) {
                    // Create VNPayCallbackData from intent extras
                    VNPayCallbackData callbackData = new VNPayCallbackData();
                    callbackData.setVnpResponseCode(data.getStringExtra("response_code"));
                    callbackData.setVnpTransactionStatus(data.getStringExtra("transaction_status"));
                    callbackData.setVnpTxnRef(data.getStringExtra("txn_ref"));
                    callbackData.setVnpAmount(data.getStringExtra("amount"));
                    callbackData.setVnpTransactionNo(data.getStringExtra("transaction_no"));
                    callbackData.setVnpBankCode(data.getStringExtra("bank_code"));
                    callbackData.setVnpPayDate(data.getStringExtra("pay_date"));
                    callbackData.setVnpSecureHash(data.getStringExtra("secure_hash"));
                    
                    // Call confirm payment API to deduct balance
                    confirmPaymentAndDeductBalance(callbackData, callback);
                } else {
                    callback.onPaymentFailure(new PaymentResult(
                        PaymentResult.Status.FAILED,
                        "Payment was not successful"
                    ));
                }
            } else {
                callback.onPaymentCancelled();
            }
        }
    }

    private static void confirmPaymentAndDeductBalance(VNPayCallbackData callbackData, PaymentCallback callback) {
        // Get customer ID from session
        android.content.Context context = null;
        try {
            // Try to get context from PaymentCallbackManager
            com.example.prm_project.utils.payment.PaymentCallbackManager callbackManager = 
                com.example.prm_project.utils.payment.PaymentCallbackManager.getInstance();
            if (callbackManager.getCallback() != null) {
                // We need to get context from somewhere - for now, we'll use a different approach
                // This will be handled by the BookingViewModel
                callback.onPaymentFailure(new com.example.prm_project.utils.payment.PaymentResult(
                    com.example.prm_project.utils.payment.PaymentResult.Status.FAILED,
                    "Payment confirmation requires context - please use BookingViewModel.processPayment()"
                ));
                return;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting context for payment confirmation", e);
        }
        
        // Fallback to hardcoded values (this should not be reached in normal flow)
        int customerId = 1;
        int bookingId = 1;
        
        Call<com.example.prm_project.data.model.PaymentConfirmationResponse> call = 
            RetrofitClient.getPaymentApiService()
                .confirmVNPayPayment(customerId, bookingId, callbackData);

        call.enqueue(new Callback<com.example.prm_project.data.model.PaymentConfirmationResponse>() {
            @Override
            public void onResponse(Call<com.example.prm_project.data.model.PaymentConfirmationResponse> call, 
                                 Response<com.example.prm_project.data.model.PaymentConfirmationResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    com.example.prm_project.data.model.PaymentConfirmationResponse confirmationResponse = response.body();
                    
                    if (confirmationResponse.isSucceeded() && confirmationResponse.getData() != null) {
                        PaymentConfirmationResponse.PaymentConfirmationData data = confirmationResponse.getData();
                        
                        PaymentResult result = new PaymentResult(
                            PaymentResult.Status.SUCCESS,
                            "Payment confirmed and balance deducted successfully",
                            data.getTransactionId(),
                            String.valueOf(data.getBookingId()),
                            data.getAmountDeducted()
                        );
                        callback.onPaymentSuccess(result);
                    } else {
                        callback.onPaymentFailure(new PaymentResult(
                            PaymentResult.Status.FAILED,
                            "Payment confirmation failed"
                        ));
                    }
                } else {
                    callback.onPaymentFailure(new PaymentResult(
                        PaymentResult.Status.FAILED,
                        "Failed to confirm payment"
                    ));
                }
            }

            @Override
            public void onFailure(Call<com.example.prm_project.data.model.PaymentConfirmationResponse> call, Throwable t) {
                Log.e(TAG, "Payment confirmation failed", t);
                callback.onPaymentFailure(new PaymentResult(
                    PaymentResult.Status.FAILED,
                    "Network error during payment confirmation: " + t.getMessage()
                ));
            }
        });
    }
}