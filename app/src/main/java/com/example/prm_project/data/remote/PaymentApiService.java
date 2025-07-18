package com.example.prm_project.data.remote;

import com.example.prm_project.data.model.PaymentConfirmationResponse;
import com.example.prm_project.utils.payment.vnpay.VNPayRequest;
import com.example.prm_project.utils.payment.vnpay.VNPayResponse;
import com.example.prm_project.utils.payment.vnpay.VNPayCallbackData;
import com.example.prm_project.utils.payment.stripe.StripeRequest;
import com.example.prm_project.utils.payment.stripe.StripeResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface PaymentApiService {
    
    // VNPay Endpoints
    @POST("payment/vnpay/create")
    Call<VNPayResponse> createVNPayPayment(@Body VNPayRequest request);
    
    @POST("payment/vnpay/confirm")
    Call<PaymentConfirmationResponse> confirmVNPayPayment(
        @Query("customerId") int customerId,
        @Query("bookingId") int bookingId,
        @Body VNPayCallbackData callbackData
    );
    
    // Stripe Endpoints
    @POST("payment/stripe/create")
    Call<StripeResponse> createStripePayment(@Body StripeRequest request);
    
    @POST("payment/stripe/confirm")
    Call<PaymentConfirmationResponse> confirmStripePayment(
        @Query("paymentIntentId") String paymentIntentId,
        @Query("customerId") int customerId,
        @Query("bookingId") int bookingId
    );
} 