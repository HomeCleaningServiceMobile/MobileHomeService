package com.example.prm_project.utils;

import android.content.Context;
import android.util.Log;
import com.example.prm_project.data.model.RefreshTokenRequest;
import com.example.prm_project.data.model.TokenResponse;
import com.example.prm_project.data.remote.AuthApiService;
import com.example.prm_project.data.remote.RetrofitClient;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class TokenManager {
    
    private static final String TAG = "TokenManager";
    private static final long TOKEN_REFRESH_THRESHOLD = 5 * 60 * 1000; // 5 minutes before expiry
    
    private SessionManager sessionManager;
    private AuthApiService authApiService;
    private boolean isRefreshing = false;
    private CountDownLatch refreshLatch;
    
    public TokenManager(Context context) {
        this.sessionManager = new SessionManager(context);
        this.authApiService = RetrofitClient.getInstance().create(AuthApiService.class);
    }
    
    /**
     * Get valid access token (auto-refresh if needed)
     */
    public String getValidAccessToken() {
        if (!sessionManager.isLoggedIn()) {
            return null;
        }
        
        // Check if token needs refresh
        if (shouldRefreshToken()) {
            String refreshedToken = refreshTokenSync();
            return refreshedToken != null ? "Bearer " + refreshedToken : null;
        }
        
        return sessionManager.getAuthToken();
    }
    
    /**
     * Check if token should be refreshed (proactive refresh)
     */
    private boolean shouldRefreshToken() {
        if (sessionManager.getAccessToken() == null) {
            return false;
        }
        
        String expiresAt = sessionManager.prefs.getString(Constants.KEY_TOKEN_EXPIRES_AT, null);
        if (expiresAt == null) {
            return true;
        }
        
//        try {
//            long expirationTime = Long.parseLong(expiresAt);
//            long currentTime = System.currentTimeMillis();
//            long timeUntilExpiry = expirationTime - currentTime;
//
//            // Refresh if token expires within threshold
//            return timeUntilExpiry <= TOKEN_REFRESH_THRESHOLD;
//        } catch (NumberFormatException e) {
//            Log.e(TAG, "Invalid expiration time format", e);
//            return true;
//        }

        try {
            long expirationTime;

            // Check if the string is already a timestamp (numeric)
            if (expiresAt.matches("\\d+")) {
                expirationTime = Long.parseLong(expiresAt);
            } else {
                // Parse ISO 8601 datetime string
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSS'Z'", Locale.US);
                sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                Date date = sdf.parse(expiresAt);
                expirationTime = date.getTime();
            }

            long currentTime = System.currentTimeMillis();
            long timeUntilExpiry = expirationTime - currentTime;

            // Refresh if token expires within threshold
            return timeUntilExpiry <= TOKEN_REFRESH_THRESHOLD;
        } catch (NumberFormatException | ParseException e) {
            Log.e(TAG, "Invalid expiration time format: " + expiresAt, e);
            return true;
        }
    }
    
    /**
     * Synchronous token refresh with concurrency control
     */
    private String refreshTokenSync() {
        // If already refreshing, wait for completion
        if (isRefreshing) {
            try {
                if (refreshLatch != null) {
                    refreshLatch.await(30, TimeUnit.SECONDS); // Wait max 30 seconds
                }
                return sessionManager.getAccessToken();
            } catch (InterruptedException e) {
                Log.e(TAG, "Token refresh wait interrupted", e);
                return null;
            }
        }
        
        // Start refresh process
        isRefreshing = true;
        refreshLatch = new CountDownLatch(1);
        
        try {
            String refreshToken = sessionManager.getRefreshToken();
            if (refreshToken == null) {
                Log.e(TAG, "No refresh token available");
                return null;
            }
            
            RefreshTokenRequest request = new RefreshTokenRequest(refreshToken);
            
            // Synchronous retrofit call
            retrofit2.Response<com.example.prm_project.data.model.ApiResponse<TokenResponse>> response = 
                authApiService.refreshToken(request).execute();
            
            if (response.isSuccessful() && response.body() != null) {
                com.example.prm_project.data.model.ApiResponse<TokenResponse> apiResponse = response.body();
                if (apiResponse.isSucceeded()) {
                    TokenResponse tokenResponse = apiResponse.getData();
                    
                    // Update session with new token
                    sessionManager.updateAccessToken(
                        tokenResponse.getToken(), 
                        tokenResponse.getExpiresAt()
                    );
                    
                    Log.d(TAG, "Token refreshed successfully");
                    return tokenResponse.getToken();
                } else {
                    Log.e(TAG, "Token refresh failed: " + apiResponse.getFirstErrorMessage());
                }
            } else {
                Log.e(TAG, "Token refresh HTTP error: " + response.code());
            }
        } catch (Exception e) {
            Log.e(TAG, "Token refresh exception", e);
        } finally {
            isRefreshing = false;
            if (refreshLatch != null) {
                refreshLatch.countDown();
            }
        }
        
        return null;
    }
    
    /**
     * Async token refresh with callback
     */
    public void refreshTokenAsync(TokenRefreshCallback callback) {
        new Thread(() -> {
            String newToken = refreshTokenSync();
            if (newToken != null) {
                callback.onSuccess(newToken);
            } else {
                callback.onError("Token refresh failed");
            }
        }).start();
    }
    
    /**
     * Force clear tokens (logout)
     */
    public void clearTokens() {
        sessionManager.clearSession();
        isRefreshing = false;
        if (refreshLatch != null) {
            refreshLatch.countDown();
        }
    }
    
    /**
     * Check if user is authenticated with valid token
     */
    public boolean isAuthenticated() {
        return sessionManager.isLoggedIn() && 
               sessionManager.getAccessToken() != null && 
               !sessionManager.isTokenExpired();
    }
    
    /**
     * Callback interface for async token refresh
     */
    public interface TokenRefreshCallback {
        void onSuccess(String newToken);
        void onError(String error);
    }
} 