package com.example.prm_project;

import android.app.Application;
import dagger.hilt.android.HiltAndroidApp;

/**
 * Application class for Hilt dependency injection
 * This enables Hilt to generate all the necessary DI components
 */
@HiltAndroidApp
public class PrmApplication extends Application {
    
    @Override
    public void onCreate() {
        super.onCreate();
        
        // Hilt automatically handles all initialization
        // No more manual AppInitializer calls needed!
    }
} 