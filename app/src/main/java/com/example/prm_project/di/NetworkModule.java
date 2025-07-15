package com.example.prm_project.di;

import android.content.Context;
import com.example.prm_project.data.remote.ApiService;
import com.example.prm_project.data.remote.AuthApiService;
import com.example.prm_project.data.remote.ServiceApiService;
import com.example.prm_project.data.remote.BookingApiService;
import com.example.prm_project.utils.AuthInterceptor;
import com.example.prm_project.utils.Constants;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.util.concurrent.TimeUnit;
import javax.inject.Named;
import javax.inject.Singleton;

/**
 * Hilt module for network dependencies
 * Provides Retrofit, OkHttp, and API services
 */
@Module
@InstallIn(SingletonComponent.class)
public class NetworkModule {
    
    /**
     * Provides HTTP logging interceptor for debugging
     */
    @Provides
    @Singleton
    public HttpLoggingInterceptor provideLoggingInterceptor() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        return interceptor;
    }
    
    /**
     * Provides authentication interceptor for automatic token management
     */
    @Provides
    @Singleton
    public AuthInterceptor provideAuthInterceptor(@ApplicationContext Context context) {
        return new AuthInterceptor(context);
    }
    
    /**
     * Provides base OkHttpClient without auth (for login, register, etc.)
     */
    @Provides
    @Singleton
    @Named("base")
    public OkHttpClient provideBaseOkHttpClient(HttpLoggingInterceptor loggingInterceptor) {
        return new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .connectTimeout(Constants.NETWORK_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(Constants.NETWORK_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(Constants.NETWORK_TIMEOUT, TimeUnit.SECONDS)
                .build();
    }
    
    /**
     * Provides authenticated OkHttpClient with automatic token management
     */
    @Provides
    @Singleton
    @Named("auth")
    public OkHttpClient provideAuthOkHttpClient(
            HttpLoggingInterceptor loggingInterceptor,
            AuthInterceptor authInterceptor) {
        return new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .addInterceptor(authInterceptor)  // Automatic token injection
                .connectTimeout(Constants.NETWORK_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(Constants.NETWORK_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(Constants.NETWORK_TIMEOUT, TimeUnit.SECONDS)
                .build();
    }
    
    /**
     * Provides base Retrofit instance (without auth)
     */
    @Provides
    @Singleton
    @Named("base")
    public Retrofit provideBaseRetrofit(@Named("base") OkHttpClient okHttpClient) {
        return new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
    
    /**
     * Provides authenticated Retrofit instance (with automatic token management)
     */
    @Provides
    @Singleton
    @Named("auth")
    public Retrofit provideAuthRetrofit(@Named("auth") OkHttpClient okHttpClient) {
        return new Retrofit.Builder()
                .baseUrl(Constants.AUTH_BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
    
    /**
     * Provides general API service
     */
    @Provides
    @Singleton
    public ApiService provideApiService(@Named("base") Retrofit retrofit) {
        return retrofit.create(ApiService.class);
    }
    
    /**
     * Provides authentication API service with automatic token management
     */
    @Provides
    @Singleton
    public AuthApiService provideAuthApiService(@Named("auth") Retrofit retrofit) {
        return retrofit.create(AuthApiService.class);
    }
    
    /**
     * Provides service API service
     */
    @Provides
    @Singleton
    public ServiceApiService provideServiceApiService(@Named("base") Retrofit retrofit) {
        return retrofit.create(ServiceApiService.class);
    }
    
    /**
     * Provides booking API service
     */
    @Provides
    @Singleton
    public BookingApiService provideBookingApiService(@Named("base") Retrofit retrofit) {
        return retrofit.create(BookingApiService.class);
    }
} 