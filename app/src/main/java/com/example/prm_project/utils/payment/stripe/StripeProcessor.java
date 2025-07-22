package com.example.prm_project.utils.payment.stripe;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import androidx.fragment.app.FragmentActivity;

import com.example.prm_project.utils.payment.PaymentProcessor;
import com.example.prm_project.utils.payment.PaymentRequest;
import com.example.prm_project.utils.payment.PaymentResult;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.paymentsheet.PaymentSheet;
import com.stripe.android.paymentsheet.PaymentSheetResult;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.POST;

public class StripeProcessor implements PaymentProcessor {
    private static final String TAG = "StripeProcessor";
    private static final String STRIPE_PUBLISHABLE_KEY = "pk_test_51RZQtlRpw5sw2xnalWMscpCULRzSPboYAjpXy2HMl1sO7T5z8kKALrlNX3hxkKsdlShdu53MnWcMujVO3zOlnmH9009frbtXBh";
    private static final String BACKEND_URL = "http://10.0.2.2:4242/";
    
    private PaymentSheet paymentSheet;
    private PaymentCallback currentCallback;
    private StripeApiService stripeApiService;

    // Stripe Backend API Interface
    interface StripeApiService {
        @POST("create-payment-intent")
        Call<StripePaymentIntentResponse> createPaymentIntent(@Body StripePaymentIntentRequest request);
    }
    
    // Request/Response models for Stripe backend
    static class StripePaymentIntentRequest {
        public final double amount;
        public final String currency;
        public final String orderId;
        public final String description;
        
        public StripePaymentIntentRequest(double amount, String currency, String orderId, String description) {
            this.amount = Math.round(amount * 100); // Convert to cents
            this.currency = currency.toLowerCase();
            this.orderId = orderId;
            this.description = description;
        }
    }
    
    static class StripePaymentIntentResponse {
        public String clientSecret;
        public String paymentIntentId;
    }

    @Override
    public PaymentMethod getPaymentMethod() {
        return PaymentMethod.STRIPE;
    }

    @Override
    public void processPayment(Context context, PaymentRequest request, PaymentCallback callback) {
        this.currentCallback = callback;
        
        if (!(context instanceof FragmentActivity)) {
            callback.onPaymentFailure(new PaymentResult(
                PaymentResult.Status.FAILED,
                "Stripe payment requires FragmentActivity context"
            ));
            return;
        }
        
        FragmentActivity activity = (FragmentActivity) context;
        
        // Initialize Stripe
        PaymentConfiguration.init(context, STRIPE_PUBLISHABLE_KEY);
        
        // Initialize PaymentSheet
        paymentSheet = new PaymentSheet(activity, this::onPaymentSheetResult);
        
        // Initialize Retrofit for backend communication
        if (stripeApiService == null) {
            Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BACKEND_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
            stripeApiService = retrofit.create(StripeApiService.class);
        }
        
        // Fetch client secret from backend
        fetchClientSecretAndPay(request);
    }
    
    private void fetchClientSecretAndPay(PaymentRequest request) {
        StripePaymentIntentRequest backendRequest = new StripePaymentIntentRequest(
            request.getAmount(),
            request.getCurrency(),
            request.getOrderId(),
            request.getDescription()
        );
        
        Log.d(TAG, "Creating payment intent for amount: " + request.getAmount());
        
        stripeApiService.createPaymentIntent(backendRequest).enqueue(new Callback<StripePaymentIntentResponse>() {
            @Override
            public void onResponse(Call<StripePaymentIntentResponse> call, Response<StripePaymentIntentResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String clientSecret = response.body().clientSecret;
                    Log.d(TAG, "Received client secret, presenting payment sheet");
                    presentPaymentSheet(clientSecret);
                } else {
                    Log.e(TAG, "Failed to create payment intent: " + response.code());
                    if (currentCallback != null) {
                        currentCallback.onPaymentFailure(new PaymentResult(
                            PaymentResult.Status.FAILED,
                            "Failed to initialize Stripe payment"
                        ));
                    }
                }
            }

            @Override
            public void onFailure(Call<StripePaymentIntentResponse> call, Throwable t) {
                Log.e(TAG, "Network error creating payment intent", t);
                if (currentCallback != null) {
                    currentCallback.onPaymentFailure(new PaymentResult(
                        PaymentResult.Status.FAILED,
                        "Network error: " + t.getMessage()
                    ));
                }
            }
        });
    }
    
    private void presentPaymentSheet(String clientSecret) {
        PaymentSheet.Configuration configuration = new PaymentSheet.Configuration.Builder("Mobile Home Service")
            .build();
            
        paymentSheet.presentWithPaymentIntent(clientSecret, configuration);
    }
    
    private void onPaymentSheetResult(PaymentSheetResult paymentSheetResult) {
        if (currentCallback == null) return;

        if (paymentSheetResult instanceof PaymentSheetResult.Completed) {
            PaymentResult result = new PaymentResult(
                PaymentResult.Status.SUCCESS,
                "Stripe payment completed successfully",
                "stripe_" + System.currentTimeMillis(),
                null,
                0.0
            );
            currentCallback.onPaymentSuccess(result);
        } else if (paymentSheetResult instanceof PaymentSheetResult.Canceled) {
            currentCallback.onPaymentCancelled();
        } else if (paymentSheetResult instanceof PaymentSheetResult.Failed) {
            PaymentSheetResult.Failed failed = (PaymentSheetResult.Failed) paymentSheetResult;
            PaymentResult result = new PaymentResult(
                PaymentResult.Status.FAILED,
                "Stripe payment failed: " + failed.getError().getLocalizedMessage(),
                null,
                null,
                0.0
            );
            currentCallback.onPaymentFailure(result);
        }
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public String getDisplayName() {
        return "Stripe";
    }

    @Override
    public void handlePaymentResult(String data, PaymentCallback callback) {
        // Not needed for native PaymentSheet implementation
    }
} 