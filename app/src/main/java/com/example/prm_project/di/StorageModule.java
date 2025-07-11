package com.example.prm_project.di;

import android.content.Context;
import com.example.prm_project.utils.SessionManager;
import com.example.prm_project.utils.TokenManager;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;
import javax.inject.Singleton;

/**
 * Hilt module for storage and session management dependencies
 * Provides encrypted storage, session management, and token management
 */
@Module
@InstallIn(SingletonComponent.class)
public class StorageModule {
    
    /**
     * Provides SessionManager with encrypted storage
     * Automatically handles migration from regular to encrypted preferences
     */
    @Provides
    @Singleton
    public SessionManager provideSessionManager(@ApplicationContext Context context) {
        return new SessionManager(context);
    }
    
    /**
     * Provides TokenManager for automatic token refresh and management
     */
    @Provides
    @Singleton
    public TokenManager provideTokenManager(@ApplicationContext Context context) {
        return new TokenManager(context);
    }
} 