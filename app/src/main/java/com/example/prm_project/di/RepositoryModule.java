package com.example.prm_project.di;

import com.example.prm_project.data.repository.AuthRepository;
import com.example.prm_project.data.repository.MainRepository;
import com.example.prm_project.data.repository.TimeSlotRepository;
import com.example.prm_project.data.remote.AuthApiService;
import com.example.prm_project.data.remote.ApiService;
import com.example.prm_project.data.remote.TimeSlotApiService;
import com.example.prm_project.utils.SessionManager;
import com.example.prm_project.utils.TokenManager;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import javax.inject.Singleton;

/**
 * Hilt module for repository dependencies
 * Provides data repositories with their required dependencies automatically injected
 */
@Module
@InstallIn(SingletonComponent.class)
public class RepositoryModule {
    
    /**
     * Provides AuthRepository with automatic dependency injection
     * All required dependencies (AuthApiService, SessionManager, TokenManager) are injected automatically
     */
    @Provides
    @Singleton
    public AuthRepository provideAuthRepository(
            AuthApiService authApiService,
            SessionManager sessionManager,
            TokenManager tokenManager) {
        return new AuthRepository(authApiService, sessionManager, tokenManager);
    }
    
    /**
     * Provides MainRepository for general data operations
     */
    @Provides
    @Singleton
    public MainRepository provideMainRepository(
            AuthApiService authApiService) {
        return new MainRepository(authApiService);
    }
    
    /**
     * Provides TimeSlotRepository for time slot operations
     */
    @Provides
    @Singleton
    public TimeSlotRepository provideTimeSlotRepository(
            TimeSlotApiService timeSlotApiService) {
        return new TimeSlotRepository(timeSlotApiService);
    }
} 