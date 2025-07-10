package com.example.prm_project.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;
import java.util.Map;

/**
 * Utility class to migrate data from regular SharedPreferences to EncryptedSharedPreferences
 * This ensures users don't lose their login sessions when upgrading to encrypted storage
 */
public class PreferencesMigration {
    
    private static final String TAG = "PreferencesMigration";
    private static final String MIGRATION_KEY = "migration_completed_v1";
    
    /**
     * Migrate data from regular SharedPreferences to EncryptedSharedPreferences
     * Only runs once per app installation
     */
    public static void migrateToEncryptedPreferences(Context context) {
        SharedPreferences regularPrefs = context.getSharedPreferences(Constants.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        
        // Check if migration already completed
        if (regularPrefs.getBoolean(MIGRATION_KEY, false)) {
            Log.d(TAG, "Migration already completed, skipping");
            return;
        }
        
        try {
            // Check if there's any data to migrate
            Map<String, ?> existingData = regularPrefs.getAll();
            if (existingData.isEmpty()) {
                Log.d(TAG, "No existing data to migrate");
                markMigrationCompleted(regularPrefs);
                return;
            }
            
            Log.d(TAG, "Starting migration of " + existingData.size() + " preferences to encrypted storage");
            
            // Create encrypted preferences
            MasterKey masterKey = new MasterKey.Builder(context)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();
            
            SharedPreferences encryptedPrefs = EncryptedSharedPreferences.create(
                    context,
                    "prm_encrypted_preferences",
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
            
            // Migrate data
            SharedPreferences.Editor encryptedEditor = encryptedPrefs.edit();
            int migratedCount = 0;
            
            for (Map.Entry<String, ?> entry : existingData.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                
                if (value instanceof String) {
                    encryptedEditor.putString(key, (String) value);
                } else if (value instanceof Boolean) {
                    encryptedEditor.putBoolean(key, (Boolean) value);
                } else if (value instanceof Integer) {
                    encryptedEditor.putInt(key, (Integer) value);
                } else if (value instanceof Long) {
                    encryptedEditor.putLong(key, (Long) value);
                } else if (value instanceof Float) {
                    encryptedEditor.putFloat(key, (Float) value);
                } else {
                    Log.w(TAG, "Unsupported data type for key: " + key + ", type: " + value.getClass().getSimpleName());
                    continue;
                }
                
                migratedCount++;
            }
            
            // Apply changes to encrypted preferences
            encryptedEditor.apply();
            
            Log.d(TAG, "Successfully migrated " + migratedCount + " preferences to encrypted storage");
            
            // Clear old data from regular preferences and mark migration as completed
            SharedPreferences.Editor regularEditor = regularPrefs.edit();
            regularEditor.clear();
            regularEditor.putBoolean(MIGRATION_KEY, true);
            regularEditor.apply();
            
            Log.d(TAG, "Migration completed successfully, old data cleared");
            
        } catch (Exception e) {
            Log.e(TAG, "Failed to migrate preferences to encrypted storage", e);
            // Mark migration as attempted to avoid infinite retry loops
            markMigrationCompleted(regularPrefs);
        }
    }
    
    /**
     * Mark migration as completed to avoid running it again
     */
    private static void markMigrationCompleted(SharedPreferences regularPrefs) {
        SharedPreferences.Editor editor = regularPrefs.edit();
        editor.putBoolean(MIGRATION_KEY, true);
        editor.apply();
    }
    
    /**
     * Check if migration has been completed
     */
    public static boolean isMigrationCompleted(Context context) {
        SharedPreferences regularPrefs = context.getSharedPreferences(Constants.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        return regularPrefs.getBoolean(MIGRATION_KEY, false);
    }
    
    /**
     * Force reset migration status (for testing purposes)
     */
    public static void resetMigrationStatus(Context context) {
        SharedPreferences regularPrefs = context.getSharedPreferences(Constants.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = regularPrefs.edit();
        editor.remove(MIGRATION_KEY);
        editor.apply();
        Log.d(TAG, "Migration status reset");
    }
} 