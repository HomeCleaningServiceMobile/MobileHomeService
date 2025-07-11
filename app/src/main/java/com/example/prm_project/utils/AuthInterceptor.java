package com.example.prm_project.utils;

import android.content.Context;
import android.util.Log;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import java.io.IOException;

public class AuthInterceptor implements Interceptor {
    
    private static final String TAG = "AuthInterceptor";
    private static final String AUTHORIZATION_HEADER = "Authorization";
    
    private TokenManager tokenManager;
    
    public AuthInterceptor(Context context) {
        this.tokenManager = new TokenManager(context);
    }
    
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();
        
        // Skip auth for login, register, and refresh endpoints
        if (shouldSkipAuth(originalRequest)) {
            return chain.proceed(originalRequest);
        }
        
        // Get valid token (with auto-refresh if needed)
        String authToken = tokenManager.getValidAccessToken();
        
        if (authToken == null) {
            Log.w(TAG, "No valid auth token available");
            return chain.proceed(originalRequest);
        }
        
        // Add Authorization header
        Request authenticatedRequest = originalRequest.newBuilder()
                .header(AUTHORIZATION_HEADER, authToken)
                .build();
        
        Response response = chain.proceed(authenticatedRequest);
        
        // Handle 401 Unauthorized - token might be expired
        if (response.code() == 401) {
            Log.d(TAG, "Received 401, attempting token refresh and retry");
            response.close(); // Close the original response
            
            // Try to refresh token and retry once
            return handleUnauthorizedResponse(chain, originalRequest);
        }
        
        return response;
    }
    
    /**
     * Handle 401 response with token refresh and retry
     */
    private Response handleUnauthorizedResponse(Chain chain, Request originalRequest) throws IOException {
        // Force token refresh
        tokenManager.refreshTokenAsync(new TokenManager.TokenRefreshCallback() {
            @Override
            public void onSuccess(String newToken) {
                Log.d(TAG, "Token refresh successful");
            }
            
            @Override
            public void onError(String error) {
                Log.e(TAG, "Token refresh failed: " + error);
            }
        });
        
        // Get the refreshed token
        String newAuthToken = tokenManager.getValidAccessToken();
        
        if (newAuthToken != null) {
            // Retry with new token
            Request retryRequest = originalRequest.newBuilder()
                    .header(AUTHORIZATION_HEADER, newAuthToken)
                    .build();
            
            Log.d(TAG, "Retrying request with refreshed token");
            return chain.proceed(retryRequest);
        } else {
            // Token refresh failed, clear session
            Log.e(TAG, "Token refresh failed, clearing session");
            tokenManager.clearTokens();
            
            // Return the original 401 response
            return chain.proceed(originalRequest);
        }
    }
    
    /**
     * Check if auth should be skipped for certain endpoints
     */
    private boolean shouldSkipAuth(Request request) {
        String url = request.url().toString();
        
        // Skip auth for these endpoints
        return url.contains("/login") ||
               url.contains("/register") ||
               url.contains("/refresh") ||
               url.contains("/forgot-password") ||
               url.contains("/reset-password");
    }
} 