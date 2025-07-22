package com.example.prm_project.data.repository;

import com.example.prm_project.data.local.LocalDataSource;
import com.example.prm_project.data.remote.RemoteDataSource;
import com.example.prm_project.data.remote.AuthApiService;
import com.example.prm_project.data.model.*;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class MainRepository {
    
    private LocalDataSource localDataSource;
    private RemoteDataSource remoteDataSource;
    private AuthApiService authApiService;
    
    /**
     * Constructor with dependency injection
     * All dependencies are automatically provided by Hilt
     */
    @Inject
    public MainRepository(AuthApiService authApiService) {
        this.localDataSource = new LocalDataSource(); // TODO: Make this injectable too
        this.remoteDataSource = new RemoteDataSource(); // TODO: Make this injectable too
        this.authApiService = authApiService;
    }
    
    // Callback interface for async operations
    public interface DataCallback {
        void onSuccess(String data);
        void onError(String error);
    }
    
    public interface UserCallback {
        void onSuccess(User user);
        void onError(String error);
    }
    
    // Authentication callback interface
    public interface AuthCallback<T> {
        void onSuccess(T data);
        void onError(String error);
    }
    
    public void getData(DataCallback callback) {
        // First try to get data from local source
        String localData = localDataSource.getCachedData();
        if (localData != null && !localData.isEmpty()) {
            callback.onSuccess(localData);
            return;
        }
        
        // If no local data, fetch from remote
        remoteDataSource.fetchData(new RemoteDataSource.RemoteCallback() {
            @Override
            public void onSuccess(String data) {
                // Cache the data locally
                localDataSource.cacheData(data);
                callback.onSuccess(data);
            }
            
            @Override
            public void onError(String error) {
                callback.onError(error);
            }
        });
    }
    
    public void getUser(int userId, UserCallback callback) {
        // First check local database
//        User localUser = localDataSource.getUser(userId);
        User localUser = null;
        if (localUser != null) {
            callback.onSuccess(localUser);
            return;
        }
        
        // If not found locally, fetch from remote
        remoteDataSource.fetchUser(userId, new RemoteDataSource.UserRemoteCallback() {
            @Override
            public void onSuccess(User user) {
                // Save to local database
                localDataSource.saveUser(user);
                callback.onSuccess(user);
            }
            
            @Override
            public void onError(String error) {
                callback.onError(error);
            }
        });
    }
    
    public void saveUser(User user) {
        localDataSource.saveUser(user);
    }
    
    public void clearCache() {
        localDataSource.clearCache();
    }
    
    // Authentication methods
    
    /**
     * Login user
     */
    public void login(LoginRequest loginRequest, AuthCallback<AuthResponse> callback) {
        Call<ApiResponse<AuthResponse>> call = authApiService.login(loginRequest);
        
        call.enqueue(new Callback<ApiResponse<AuthResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<AuthResponse>> call, Response<ApiResponse<AuthResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<AuthResponse> apiResponse = response.body();
                    if (apiResponse.isSucceeded()) {
                        callback.onSuccess(apiResponse.getData());
                    } else {
                        callback.onError(apiResponse.getFirstErrorMessage());
                    }
                } else {
                    callback.onError("Login failed. Please try again.");
                }
            }
            
            @Override
            public void onFailure(Call<ApiResponse<AuthResponse>> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }
    
    /**
     * Forgot password
     */
    public void forgotPassword(ForgotPasswordRequest request, AuthCallback<Void> callback) {
        Call<ApiResponse<Void>> call = authApiService.forgotPassword(request);
        
        call.enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Void> apiResponse = response.body();
                    if (apiResponse.isSucceeded()) {
                        callback.onSuccess(null);
                    } else {
                        callback.onError(apiResponse.getFirstErrorMessage());
                    }
                } else {
                    callback.onError("Failed to send reset email. Please try again.");
                }
            }
            
            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }
    
    /**
     * Reset password
     */
    public void resetPassword(ResetPasswordRequest request, AuthCallback<Void> callback) {
        Call<ApiResponse<Void>> call = authApiService.resetPassword(request);
        
        call.enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Void> apiResponse = response.body();
                    if (apiResponse.isSucceeded()) {
                        callback.onSuccess(null);
                    } else {
                        callback.onError(apiResponse.getFirstErrorMessage());
                    }
                } else {
                    callback.onError("Failed to reset password. Please try again.");
                }
            }
            
            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }
    
    /**
     * Change password
     */
    public void changePassword(String authToken, ChangePasswordRequest request, AuthCallback<Void> callback) {
        Call<ApiResponse<Void>> call = authApiService.changePassword(authToken, request);
        
        call.enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Void> apiResponse = response.body();
                    if (apiResponse.isSucceeded()) {
                        callback.onSuccess(null);
                    } else {
                        callback.onError(apiResponse.getFirstErrorMessage());
                    }
                } else {
                    callback.onError("Failed to change password. Please try again.");
                }
            }
            
            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }
    
    /**
     * Get user profile
     */
    public void getProfile(Callback<AppResponse<UserResponse>> callback) {
        authApiService.getProfile().enqueue(callback);
    }

    /**
     * Refresh token
     */
    public void refreshToken(RefreshTokenRequest request, AuthCallback<TokenResponse> callback) {
        Call<ApiResponse<TokenResponse>> call = authApiService.refreshToken(request);
        
        call.enqueue(new Callback<ApiResponse<TokenResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<TokenResponse>> call, Response<ApiResponse<TokenResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<TokenResponse> apiResponse = response.body();
                    if (apiResponse.isSucceeded()) {
                        callback.onSuccess(apiResponse.getData());
                    } else {
                        callback.onError(apiResponse.getFirstErrorMessage());
                    }
                } else {
                    callback.onError("Failed to refresh token. Please login again.");
                }
            }
            
            @Override
            public void onFailure(Call<ApiResponse<TokenResponse>> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }
    
    /**
     * Logout user
     */
    public void logout(String authToken, AuthCallback<String> callback) {
        Call<ApiResponse<String>> call = authApiService.logout("Bearer " + authToken);

        call.enqueue(new Callback<ApiResponse<String>>() {
            @Override
            public void onResponse(Call<ApiResponse<String>> call, Response<ApiResponse<String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<String> apiResponse = response.body();
                    if (apiResponse.isSucceeded()) {
                        // ✅ Clear local cache/session only after successful logout
                        localDataSource.clearCache();
                        callback.onSuccess(apiResponse.getData() != null
                                ? apiResponse.getData()
                                : "Logged out successfully");
                    } else {
                        callback.onError(apiResponse.getFirstErrorMessage());
                    }
                } else {
                    callback.onError("Logout failed. Please try again.");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<String>> call, Throwable t) { // ✅ Correct type
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }
} 