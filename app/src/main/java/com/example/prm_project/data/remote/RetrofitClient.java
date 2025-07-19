package com.example.prm_project.data.remote;

import android.content.Context;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import com.example.prm_project.utils.Constants;
import com.example.prm_project.utils.AuthInterceptor;
import java.util.concurrent.TimeUnit;

public class RetrofitClient {

    private static Retrofit retrofit = null;
    private static Retrofit authRetrofit = null;
    private static Context applicationContext = null;

    /**
     * Initialize RetrofitClient with application context
     * Must be called before using authenticated endpoints
     */
    public static void initialize(Context context) {
        applicationContext = context.getApplicationContext();
        // Reset instances to recreate with auth interceptor
        retrofit = null;
        authRetrofit = null;
    }

    public static Retrofit getInstance() {
        if (retrofit == null) {
            retrofit = createRetrofitInstance(Constants.BASE_URL, false);
        }
        return retrofit;
    }

    public static Retrofit getAuthInstance() {
        if (authRetrofit == null) {
            authRetrofit = createRetrofitInstance(Constants.AUTH_BASE_URL, true);
        }
        return authRetrofit;
    }

    private static Retrofit createRetrofitInstance(String baseUrl, boolean includeAuth) {
        // Create logging interceptor
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        // Create OkHttpClient builder
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .connectTimeout(Constants.NETWORK_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(Constants.NETWORK_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(Constants.NETWORK_TIMEOUT, TimeUnit.SECONDS);
        
        // Add auth interceptor for authenticated endpoints
        if (includeAuth && applicationContext != null) {
            clientBuilder.addInterceptor(new AuthInterceptor(applicationContext));
        }

        OkHttpClient okHttpClient = clientBuilder.build();

        // Create Retrofit instance
        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static ApiService getApiService() {
        return getInstance().create(ApiService.class);
    }

    public static AuthApiService getAuthApiService() {
        return getAuthInstance().create(AuthApiService.class);
    }

    public static PaymentApiService getPaymentApiService() {
        return getAuthInstance().create(PaymentApiService.class);
    }

    public static void setBaseUrl(String baseUrl) {
        retrofit = createRetrofitInstance(baseUrl, false);
    }

    public static BookingApiService getBookingApiService() {
        return getInstance().create(BookingApiService.class);
    }

}