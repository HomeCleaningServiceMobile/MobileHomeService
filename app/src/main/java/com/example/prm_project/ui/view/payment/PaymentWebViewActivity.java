package com.example.prm_project.ui.view.payment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
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
    }

    private void setupWebView() {
        WebView webView = binding.webView;
        
        // Enable JavaScript
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);

        // Set WebViewClient to handle URL navigation
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                String url = request.getUrl().toString();
                Log.d(TAG, "Loading URL: " + url);

                // Check if this is a return URL (payment completed)
                if (url.contains("payment/success") || url.contains("payment/cancel") || 
                    url.contains("vnp_ResponseCode")) {
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
                // You can show progress here if needed
            }
        });
    }

    private void loadPaymentUrl() {
        Log.d(TAG, "Loading payment URL: " + paymentUrl);
        binding.webView.loadUrl(paymentUrl);
    }

    private void handlePaymentResult(String url) {
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

            // Create result data
            Intent resultIntent = new Intent();
            resultIntent.putExtra("response_code", responseCode);
            resultIntent.putExtra("transaction_status", transactionStatus);
            resultIntent.putExtra("txn_ref", txnRef);
            resultIntent.putExtra("amount", amount);
            resultIntent.putExtra("transaction_no", transactionNo);
            resultIntent.putExtra("bank_code", bankCode);
            resultIntent.putExtra("pay_date", payDate);
            resultIntent.putExtra("secure_hash", secureHash);
            resultIntent.putExtra("order_id", orderId);
            resultIntent.putExtra("payment_amount", this.amount);

            // Check if payment was successful
            boolean isSuccess = "00".equals(responseCode) && "00".equals(transactionStatus);
            resultIntent.putExtra("payment_success", isSuccess);

            setResult(RESULT_OK, resultIntent);
            finish();

        } catch (Exception e) {
            Log.e(TAG, "Error parsing payment result", e);
            Toast.makeText(this, "Error processing payment result", Toast.LENGTH_SHORT).show();
            setResult(RESULT_CANCELED);
            finish();
        }
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
            setResult(RESULT_CANCELED);
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (binding != null) {
            binding.webView.destroy();
        }
    }
} 