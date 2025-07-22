package com.example.prm_project.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;
import com.example.prm_project.data.model.User;
import com.example.prm_project.data.model.AuthResponse;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.Instant;
import java.time.format.DateTimeParseException;

public class SessionManager {
    
    private static final String TAG = "SessionManager";
    private static final String ENCRYPTED_PREFS_NAME = "prm_encrypted_preferences";
    
    SharedPreferences prefs; // Package-private for TokenManager access
    private SharedPreferences.Editor editor;
    private boolean isEncrypted = false;
    
    public SessionManager(Context context) {
        try {
            // Try to create encrypted shared preferences
            prefs = createEncryptedSharedPreferences(context);
            isEncrypted = true;
            Log.d(TAG, "Using EncryptedSharedPreferences for secure token storage");
        } catch (Exception e) {
            Log.w(TAG, "Failed to create EncryptedSharedPreferences, falling back to regular SharedPreferences", e);
            // Fallback to regular SharedPreferences
            prefs = context.getSharedPreferences(Constants.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
            isEncrypted = false;
        }
        
        editor = prefs.edit();
    }
    
    /**
     * Create encrypted shared preferences with AES256 encryption
     */
    private SharedPreferences createEncryptedSharedPreferences(Context context) 
            throws GeneralSecurityException, IOException {
        
        // Create or retrieve the master key for encryption
        MasterKey masterKey = new MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build();
        
        // Create EncryptedSharedPreferences
        return EncryptedSharedPreferences.create(
                context,
                ENCRYPTED_PREFS_NAME,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        );
    }
    
    /**
     * Save user session after successful login
     */
    public void saveUserSession(AuthResponse authResponse, boolean rememberMe) {
        User user = authResponse.getUser();
        
        try {
            editor.putInt(Constants.KEY_USER_ID, user.getId());
            editor.putString(Constants.KEY_USER_EMAIL, user.getEmail());
            editor.putString(Constants.KEY_USER_NAME, user.getFullName());
            editor.putString(Constants.KEY_USER_ROLE, user.getRole());
            editor.putString(Constants.KEY_ACCESS_TOKEN, authResponse.getAccessToken());
            editor.putString(Constants.KEY_REFRESH_TOKEN, authResponse.getRefreshToken());
            editor.putString(Constants.KEY_TOKEN_EXPIRES_AT, authResponse.getExpiresAt());
            editor.putBoolean(Constants.KEY_IS_LOGGED_IN, true);
            editor.putBoolean(Constants.KEY_REMEMBER_ME, rememberMe);
            editor.putLong(Constants.KEY_LAST_SYNC, System.currentTimeMillis());
            editor.apply();
            
            Log.d(TAG, "User session saved successfully" + (isEncrypted ? " (encrypted)" : " (unencrypted)"));
            Log.d(TAG, "User role saved: " + user.getRole());
        } catch (Exception e) {
            Log.e(TAG, "Failed to save user session", e);
        }
    }
    
    /**
     * Update access token after refresh
     */
    public void updateAccessToken(String newToken, String expiresAt) {
        try {
            editor.putString(Constants.KEY_ACCESS_TOKEN, newToken);
            editor.putString(Constants.KEY_TOKEN_EXPIRES_AT, expiresAt);
            editor.putLong(Constants.KEY_LAST_SYNC, System.currentTimeMillis());
            editor.apply();
            
            Log.d(TAG, "Access token updated successfully" + (isEncrypted ? " (encrypted)" : " (unencrypted)"));
        } catch (Exception e) {
            Log.e(TAG, "Failed to update access token", e);
        }
    }
    
    /**
     * Get access token with Bearer prefix
     */
    public String getAuthToken() {
        try {
            String token = prefs.getString(Constants.KEY_ACCESS_TOKEN, null);
            return token != null ? "Bearer " + token : null;
        } catch (Exception e) {
            Log.e(TAG, "Failed to retrieve auth token", e);
            return null;
        }
    }
    
    /**
     * Get raw access token
     */
    public String getAccessToken() {
        try {
            return prefs.getString(Constants.KEY_ACCESS_TOKEN, null);
        } catch (Exception e) {
            Log.e(TAG, "Failed to retrieve access token", e);
            return null;
        }
    }
    
    /**
     * Get refresh token
     */
    public String getRefreshToken() {
        try {
            return prefs.getString(Constants.KEY_REFRESH_TOKEN, null);
        } catch (Exception e) {
            Log.e(TAG, "Failed to retrieve refresh token", e);
            return null;
        }
    }
    
    /**
     * Check if user is logged in
     */
    public boolean isLoggedIn() {
        try {
            return prefs.getBoolean(Constants.KEY_IS_LOGGED_IN, false) && 
                   getAccessToken() != null;
        } catch (Exception e) {
            Log.e(TAG, "Failed to check login status", e);
            return false;
        }
    }
    
    /**
     * Check if remember me is enabled
     */
    public boolean isRememberMeEnabled() {
        try {
            return prefs.getBoolean(Constants.KEY_REMEMBER_ME, false);
        } catch (Exception e) {
            Log.e(TAG, "Failed to check remember me status", e);
            return false;
        }
    }
    
    /**
     * Get user ID
     */
    public int getUserId() {
        try {
            return prefs.getInt(Constants.KEY_USER_ID, -1);
        } catch (Exception e) {
            Log.e(TAG, "Failed to retrieve user ID", e);
            return -1;
        }
    }
    
    /**
     * Get current customer ID (same as user ID for customers)
     */
    public int getCurrentCustomerId() {
        try {
            return prefs.getInt(Constants.KEY_USER_ID, -1);
        } catch (Exception e) {
            Log.e(TAG, "Failed to retrieve customer ID", e);
            return -1;
        }
    }
    
    /**
     * Get user email
     */
    public String getUserEmail() {
        try {
            return prefs.getString(Constants.KEY_USER_EMAIL, null);
        } catch (Exception e) {
            Log.e(TAG, "Failed to retrieve user email", e);
            return null;
        }
    }
    
    /**
     * Get user name
     */
    public String getUserName() {
        try {
            return prefs.getString(Constants.KEY_USER_NAME, null);
        } catch (Exception e) {
            Log.e(TAG, "Failed to retrieve user name", e);
            return null;
        }
    }
    
    /**
     * Get user role
     */
    public String getUserRole() {
        try {
            String role = prefs.getString(Constants.KEY_USER_ROLE, null);
            Log.d(TAG, "getUserRole() called, retrieved role: " + role);
            return role;
        } catch (Exception e) {
            Log.e(TAG, "Failed to retrieve user role", e);
            return null;
        }
    }
    
    /**
     * Check if token is expired (basic check)
     */
//    public boolean isTokenExpired() {
//        try {
//            String expiresAt = prefs.getString(Constants.KEY_TOKEN_EXPIRES_AT, null);
//            if (expiresAt == null) return true;
//
//            // Simple time-based check - in production you might want to parse the actual date
//            long expirationTime = Long.parseLong(expiresAt);
//            return System.currentTimeMillis() > expirationTime;
//        } catch (Exception e) {
//            Log.e(TAG, "Failed to check token expiration", e);
//            return true; // If we can't parse, assume expired
//        }
//    }
    public boolean isTokenExpired() {
        try {
            String expiresAt = prefs.getString(Constants.KEY_TOKEN_EXPIRES_AT, null);
            if (expiresAt == null || expiresAt.trim().isEmpty()) {
                return true; // No expiration date, assume expired
            }

            // Parse ISO 8601 date-time string
            Instant expirationInstant = Instant.parse(expiresAt);
            long expirationMillis = expirationInstant.toEpochMilli();
            long currentMillis = System.currentTimeMillis();
//            return currentMillis > expirationMillis;
            return true;
        } catch (DateTimeParseException e) {
            Log.e(TAG, "Failed to parse token expiration: " + e.getMessage(), e);
            return true; // Assume expired if parsing fails
        }
    }
    
    /**
     * Clear user session (logout)
     */
    public void clearSession() {
        try {
            editor.clear();
            editor.apply();
            Log.d(TAG, "User session cleared successfully" + (isEncrypted ? " (encrypted storage)" : " (regular storage)"));
        } catch (Exception e) {
            Log.e(TAG, "Failed to clear user session", e);
        }
    }
    
    /**
     * Save user email for auto-fill (remember email)
     */
    public void saveUserEmail(String email) {
        try {
            editor.putString(Constants.KEY_USER_EMAIL, email);
            editor.apply();
        } catch (Exception e) {
            Log.e(TAG, "Failed to save user email", e);
        }
    }
    
    /**
     * Get saved email for auto-fill
     */
    public String getSavedEmail() {
        try {
            return prefs.getString(Constants.KEY_USER_EMAIL, "");
        } catch (Exception e) {
            Log.e(TAG, "Failed to retrieve saved email", e);
            return "";
        }
    }
    
    /**
     * Check if encrypted storage is being used
     */
    public boolean isUsingEncryption() {
        return isEncrypted;
    }
    
    /**
     * Get storage info for debugging/monitoring
     */
    public String getStorageInfo() {
        return isEncrypted ? 
            "EncryptedSharedPreferences (AES256-GCM)" : 
            "Regular SharedPreferences";
    }
} 