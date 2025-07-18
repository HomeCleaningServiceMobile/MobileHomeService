package com.example.prm_project.utils.payment.stripe;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.example.prm_project.utils.payment.PaymentProcessor;
import com.example.prm_project.utils.payment.PaymentRequest;
import com.example.prm_project.utils.payment.PaymentResult;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.paymentsheet.PaymentSheet;
import com.stripe.android.paymentsheet.PaymentSheetResult;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class StripeProcessor implements PaymentProcessor {
    private static final String STRIPE_PUBLISHABLE_KEY = "pk_test_51RZQtlRpw5sw2xnalWMscpCULRzSPboYAjpXy2HMl1sO7T5z8kKALrlNX3hxkKsdlShdu53MnWcMujVO3zOlnmH9009frbtXBh";
    // Note: This should be updated to use the proper API endpoint
    // For now, we'll use a placeholder that will be handled by the BookingViewModel
    
    private PaymentSheet paymentSheet;
    private PaymentCallback currentCallback;

    @Override
    public PaymentMethod getPaymentMethod() {
        return PaymentMethod.STRIPE;
    }

    @Override
    public void processPayment(Context context, PaymentRequest request, PaymentCallback callback) {
        this.currentCallback = callback;
        
        // Initialize Stripe
        PaymentConfiguration.init(context, STRIPE_PUBLISHABLE_KEY);

        // Create PaymentSheet if not exists
        if (paymentSheet == null && context instanceof androidx.fragment.app.FragmentActivity) {
            paymentSheet = new PaymentSheet(
                (androidx.fragment.app.FragmentActivity) context, 
                this::onPaymentSheetResult
            );
        }

        // Fetch client secret and present payment sheet
        fetchClientSecretAndPay(request);
    }

    @Override
    public boolean isAvailable() {
        return true; // Stripe is always available
    }

    @Override
    public String getDisplayName() {
        return "Stripe";
    }

    @Override
    public void handlePaymentResult(String data, PaymentCallback callback) {
        // Handle any external payment result if needed
        // For Stripe, results are handled through PaymentSheet callback
    }

    private void fetchClientSecretAndPay(PaymentRequest request) {
        // For now, we'll simulate a successful payment since the backend API needs to be configured
        // In a real implementation, this would call the Stripe API endpoint
        
        // Simulate API call delay
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (currentCallback != null) {
                PaymentResult result = new PaymentResult(
                    PaymentResult.Status.SUCCESS,
                    "Stripe payment completed successfully (simulated)",
                    "stripe_" + System.currentTimeMillis(),
                    request.getOrderId(),
                    request.getAmount()
                );
                currentCallback.onPaymentSuccess(result);
            }
        }, 2000); // 2 second delay to simulate processing
    }

    private void presentPaymentSheet(String clientSecret) {
        if (paymentSheet != null) {
            PaymentSheet.Configuration configuration = new PaymentSheet.Configuration.Builder("Demo Store")
                .build();
                
            paymentSheet.presentWithPaymentIntent(clientSecret, configuration);
        }
    }

    private void onPaymentSheetResult(PaymentSheetResult paymentSheetResult) {
        if (currentCallback == null) return;

        if (paymentSheetResult instanceof PaymentSheetResult.Completed) {
            PaymentResult result = new PaymentResult(
                PaymentResult.Status.SUCCESS,
                "Payment completed successfully",
                "stripe_" + System.currentTimeMillis(),
                null, // orderId will be set by the caller
                0.0   // amount will be set by the caller
            );
            currentCallback.onPaymentSuccess(result);
        } else if (paymentSheetResult instanceof PaymentSheetResult.Canceled) {
            currentCallback.onPaymentCancelled();
        } else if (paymentSheetResult instanceof PaymentSheetResult.Failed) {
            PaymentSheetResult.Failed failed = (PaymentSheetResult.Failed) paymentSheetResult;
            PaymentResult result = new PaymentResult(
                PaymentResult.Status.FAILED,
                "Payment failed: " + failed.getError().getLocalizedMessage(),
                null,
                null,
                0.0
            );
            currentCallback.onPaymentFailure(result);
        }
    }
} 