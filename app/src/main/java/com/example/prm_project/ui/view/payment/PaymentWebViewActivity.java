package com.example.prm_project.ui.view.payment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.prm_project.R;
import com.example.prm_project.databinding.ActivityPaymentWebviewBinding;

public class PaymentWebViewActivity extends AppCompatActivity {
    private static final String TAG = "PaymentWebViewActivity";
    private ActivityPaymentWebviewBinding binding;
    private String paymentUrl;
    private String orderId;
    private double amount;
    private Handler paymentMonitorHandler;
    private Runnable paymentMonitorRunnable;
    private boolean paymentCompleted = false;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPaymentWebviewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Get data from intent
        paymentUrl = getIntent().getStringExtra("payment_url");
        orderId = getIntent().getStringExtra("order_id");
        amount = getIntent().getDoubleExtra("amount", 0.0);

        if (paymentUrl == null) {
            Toast.makeText(this, "Invalid payment URL", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        setupWebView();
        loadPaymentUrl();
        startPaymentMonitoring();
    }

    private void setupWebView() {
        WebView webView = binding.webView;
        
        // Enable JavaScript and other settings
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);
        webView.getSettings().setAllowFileAccess(false);
        webView.getSettings().setAllowContentAccess(false);
        
        // Add JavaScript interface for communication
        webView.addJavascriptInterface(new PaymentJavaScriptInterface(), "AndroidPayment");

        // Set WebViewClient to handle URL navigation and inject JavaScript
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                String url = request.getUrl().toString();
                Log.d(TAG, "Loading URL: " + url);

                // Check if this is a deep link return (vnpay://)
                if (url.startsWith("vnpay://")) {
                    Log.d(TAG, "Deep link detected, letting system handle: " + url);
                    // Let the system handle the deep link - this will trigger PaymentCallbackActivity
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    // Close this WebView activity since deep link will handle the result
                    finish();
                    return true;
                }

                // Check if this is a VNPay callback URL (backend API or web-based)
                if (url.contains("vnp_ResponseCode") || url.contains("vnp_TransactionStatus") || 
                    url.contains("/api/payment/vnpay/confirm") || url.contains("payment/success") || 
                    url.contains("payment/cancel")) {
                    Log.d(TAG, "VNPay callback detected: " + url);
                    handlePaymentResult(url);
                    return true;
                }

                // Load the URL in the WebView
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Log.d(TAG, "Page loaded: " + url);
                
                // Inject JavaScript for payment monitoring
                injectPaymentMonitoringScript(view, url);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                Log.e(TAG, "WebView error: " + error.getDescription());
                Toast.makeText(PaymentWebViewActivity.this, 
                    "Payment page error: " + error.getDescription(), 
                    Toast.LENGTH_SHORT).show();
            }
        });

        // Set WebChromeClient for progress updates
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                // Update progress bar if you have one
                Log.d(TAG, "Page loading progress: " + newProgress + "%");
            }
            
            @Override
            public boolean onConsoleMessage(android.webkit.ConsoleMessage consoleMessage) {
                Log.d(TAG, "Console: " + consoleMessage.message());
                return super.onConsoleMessage(consoleMessage);
            }
        });
    }
    
    private void injectPaymentMonitoringScript(WebView webView, String url) {
        // JavaScript to monitor payment status and form submissions
        String javascript = 
            "(function() {" +
            "  console.log('Payment monitoring script injected');" +
            "  " +
            "  // Monitor for VNPay specific elements and responses" +
            "  function checkPaymentStatus() {" +
            "    var urlParams = new URLSearchParams(window.location.search);" +
            "    var responseCode = urlParams.get('vnp_ResponseCode');" +
            "    var transactionStatus = urlParams.get('vnp_TransactionStatus');" +
            "    " +
            "    if (responseCode && transactionStatus) {" +
            "      console.log('Payment result detected: ' + responseCode + ', ' + transactionStatus);" +
            "      AndroidPayment.onPaymentResult(window.location.href);" +
            "      return;" +
            "    }" +
            "    " +
            "    // Check for success/failure indicators in the page" +
            "    var successElements = document.querySelectorAll('[class*=\"success\"], [id*=\"success\"], [class*=\"complete\"]');" +
            "    var failureElements = document.querySelectorAll('[class*=\"error\"], [id*=\"error\"], [class*=\"fail\"]');" +
            "    " +
            "    if (successElements.length > 0) {" +
            "      console.log('Payment success elements detected');" +
            "      AndroidPayment.onPaymentSuccess('Payment completed successfully');" +
            "    } else if (failureElements.length > 0) {" +
            "      console.log('Payment failure elements detected');" +
            "      AndroidPayment.onPaymentFailure('Payment failed');" +
            "    }" +
            "  }" +
            "  " +
            "  // Monitor form submissions" +
            "  document.addEventListener('submit', function(e) {" +
            "    console.log('Form submitted:', e.target.action);" +
            "    AndroidPayment.onFormSubmitted(e.target.action || window.location.href);" +
            "  });" +
            "  " +
            "  // Monitor button clicks" +
            "  document.addEventListener('click', function(e) {" +
            "    if (e.target.type === 'submit' || e.target.tagName === 'BUTTON') {" +
            "      console.log('Payment button clicked:', e.target.textContent);" +
            "      AndroidPayment.onButtonClicked(e.target.textContent || 'Unknown');" +
            "    }" +
            "  });" +
            "  " +
            "  // Check payment status immediately and periodically" +
            "  checkPaymentStatus();" +
            "  setInterval(checkPaymentStatus, 2000);" +
            "  " +
            "  // Monitor URL changes" +
            "  var currentUrl = window.location.href;" +
            "  setInterval(function() {" +
            "    if (window.location.href !== currentUrl) {" +
            "      currentUrl = window.location.href;" +
            "      console.log('URL changed to:', currentUrl);" +
            "      AndroidPayment.onUrlChanged(currentUrl);" +
            "      checkPaymentStatus();" +
            "    }" +
            "  }, 1000);" +
            "})();";
        
        webView.evaluateJavascript(javascript, null);
    }
    
    // JavaScript Interface for communication between WebView and Android
    public class PaymentJavaScriptInterface {
        @JavascriptInterface
        public void onPaymentResult(String url) {
            Log.d(TAG, "JavaScript detected payment result: " + url);
            runOnUiThread(() -> handlePaymentResult(url));
        }
        
        @JavascriptInterface
        public void onPaymentSuccess(String message) {
            Log.d(TAG, "JavaScript detected payment success: " + message);
            runOnUiThread(() -> {
                if (!paymentCompleted) {
                    paymentCompleted = true;
                    finishWithResult(true, message);
                }
            });
        }
        
        @JavascriptInterface
        public void onPaymentFailure(String message) {
            Log.d(TAG, "JavaScript detected payment failure: " + message);
            runOnUiThread(() -> {
                if (!paymentCompleted) {
                    paymentCompleted = true;
                    finishWithResult(false, message);
                }
            });
        }
        
        @JavascriptInterface
        public void onFormSubmitted(String action) {
            Log.d(TAG, "Form submitted to: " + action);
            // You can add additional logic here if needed
        }
        
        @JavascriptInterface
        public void onButtonClicked(String buttonText) {
            Log.d(TAG, "Button clicked: " + buttonText);
            // You can add additional logic here if needed
        }
        
        @JavascriptInterface
        public void onUrlChanged(String newUrl) {
            Log.d(TAG, "URL changed to: " + newUrl);
            runOnUiThread(() -> {
                // Check if the new URL indicates payment completion
                if (newUrl.contains("vnp_ResponseCode") || newUrl.contains("payment/result")) {
                    handlePaymentResult(newUrl);
                }
            });
        }
    }
    
    private void startPaymentMonitoring() {
        paymentMonitorHandler = new Handler(Looper.getMainLooper());
        paymentMonitorRunnable = new Runnable() {
            @Override
            public void run() {
                if (!paymentCompleted) {
                    // Check current URL for payment result
                    binding.webView.evaluateJavascript(
                        "window.location.href", 
                        url -> {
                            if (url != null && !url.equals("null")) {
                                String cleanUrl = url.replace("\"", "");
                                if (cleanUrl.contains("vnp_ResponseCode")) {
                                    handlePaymentResult(cleanUrl);
                                }
                            }
                        }
                    );
                    
                    // Schedule next check
                    paymentMonitorHandler.postDelayed(this, 3000);
                }
            }
        };
        paymentMonitorHandler.postDelayed(paymentMonitorRunnable, 5000); // Start after 5 seconds
    }

    private void loadPaymentUrl() {
        Log.d(TAG, "Loading payment URL: " + paymentUrl);
        binding.webView.loadUrl(paymentUrl);
    }

    private void handlePaymentResult(String url) {
        if (paymentCompleted) return;
        
        Log.d(TAG, "Handling payment result: " + url);
        
        try {
            Uri uri = Uri.parse(url);
            
            // Extract VNPay parameters
            String responseCode = uri.getQueryParameter("vnp_ResponseCode");
            String transactionStatus = uri.getQueryParameter("vnp_TransactionStatus");
            String txnRef = uri.getQueryParameter("vnp_TxnRef");
            String amount = uri.getQueryParameter("vnp_Amount");
            String transactionNo = uri.getQueryParameter("vnp_TransactionNo");
            String bankCode = uri.getQueryParameter("vnp_BankCode");
            String payDate = uri.getQueryParameter("vnp_PayDate");
            String secureHash = uri.getQueryParameter("vnp_SecureHash");

            Log.d(TAG, "Payment callback - Response Code: " + responseCode + ", Transaction Status: " + transactionStatus);
            
            // Check if payment was successful
            boolean isSuccess = "00".equals(responseCode) && "00".equals(transactionStatus);
            
            paymentCompleted = true;
            
            if (isSuccess) {
                // Create VNPayCallbackData and confirm payment with backend
                com.example.prm_project.utils.payment.vnpay.VNPayCallbackData callbackData = 
                    new com.example.prm_project.utils.payment.vnpay.VNPayCallbackData();
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
            Log.e(TAG, "Error parsing payment result", e);
            Toast.makeText(this, "Error processing payment result", Toast.LENGTH_SHORT).show();
            navigateToFailure();
        }
    }
    
    private void confirmPaymentWithBackend(com.example.prm_project.utils.payment.vnpay.VNPayCallbackData callbackData) {
        // Get customer ID from session
        com.example.prm_project.utils.SessionManager sessionManager = 
            new com.example.prm_project.utils.SessionManager(this);
        int customerId = sessionManager.getCurrentCustomerId();
        
        if (customerId == -1) {
            Log.e(TAG, "Customer not found - cannot confirm payment");
            navigateToFailure();
            return;
        }
        
        // For now, use a placeholder booking ID - in real implementation,
        // you would store the booking ID when starting payment
        int bookingId = 1; // This should come from the payment initiation
        
        retrofit2.Call<com.example.prm_project.data.model.PaymentConfirmationResponse> call = 
            com.example.prm_project.data.remote.RetrofitClient.getPaymentApiService()
                .confirmVNPayPayment(customerId, bookingId, callbackData);

        call.enqueue(new retrofit2.Callback<com.example.prm_project.data.model.PaymentConfirmationResponse>() {
            @Override
            public void onResponse(retrofit2.Call<com.example.prm_project.data.model.PaymentConfirmationResponse> call, 
                                 retrofit2.Response<com.example.prm_project.data.model.PaymentConfirmationResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    com.example.prm_project.data.model.PaymentConfirmationResponse confirmationResponse = response.body();
                    
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
            public void onFailure(retrofit2.Call<com.example.prm_project.data.model.PaymentConfirmationResponse> call, Throwable t) {
                Log.e(TAG, "Network error during payment confirmation", t);
                navigateToFailure();
            }
        });
    }
    
    private void navigateToSuccess() {
        Log.d(TAG, "Navigating to success screen");
        
        // Navigate to main activity with success flag
        Intent intent = new Intent(this, com.example.prm_project.ui.view.MainActivity.class);
        intent.putExtra("navigate_to", "payment_success");
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
    
    private void navigateToFailure() {
        Log.d(TAG, "Navigating to failure screen");
        
        // Navigate to main activity with failure flag
        Intent intent = new Intent(this, com.example.prm_project.ui.view.MainActivity.class);
        intent.putExtra("navigate_to", "payment_failure");
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
    
    private void finishWithResult(boolean success, String message) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("payment_success", success);
        resultIntent.putExtra("payment_message", message);
        resultIntent.putExtra("order_id", orderId);
        resultIntent.putExtra("payment_amount", amount);
        
        setResult(success ? RESULT_OK : RESULT_CANCELED, resultIntent);
        finish();
    }

    public void onBackButtonClick(android.view.View view) {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        if (binding.webView.canGoBack()) {
            binding.webView.goBack();
        } else {
            // User cancelled payment
            paymentCompleted = true;
            setResult(RESULT_CANCELED);
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        // Cancel payment monitoring
        if (paymentMonitorHandler != null && paymentMonitorRunnable != null) {
            paymentMonitorHandler.removeCallbacks(paymentMonitorRunnable);
        }
        
        if (binding != null && binding.webView != null) {
            // Remove WebView from its parent before destroying
            ViewGroup parent = (ViewGroup) binding.webView.getParent();
            if (parent != null) {
                parent.removeView(binding.webView);
            }
            binding.webView.destroy();
        }
        super.onDestroy();
    }
} 