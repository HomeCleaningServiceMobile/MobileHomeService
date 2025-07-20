package com.example.prm_project.data.repository;

import com.example.prm_project.data.model.*;
import com.example.prm_project.data.remote.AuthApiService;
import com.example.prm_project.utils.SessionManager;
import com.example.prm_project.utils.TokenManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class AuthRepository {
    
    private AuthApiService authApiService;
    private SessionManager sessionManager;
    private TokenManager tokenManager;
    
    /**
     * Constructor with dependency injection
     * All dependencies are automatically provided by Hilt
     */
    @Inject
    public AuthRepository(
            AuthApiService authApiService,
            SessionManager sessionManager,
            TokenManager tokenManager) {
        this.authApiService = authApiService;
        this.sessionManager = sessionManager;
        this.tokenManager = tokenManager;
    }
    
    // Callback interfaces
    public interface AuthCallback<T> {
        void onSuccess(T data);
        void onError(String error);
    }
    
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
     * Register customer
     */
    public void registerCustomer(CustomerRegistrationRequest request, AuthCallback<AuthResponse> callback) {
        Call<ApiResponse<AuthResponse>> call = authApiService.registerCustomer(request);
        
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
                    callback.onError("Registration failed. Please try again.");
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
     * Change password - No manual token needed! AuthInterceptor handles it
     */
    public void changePassword(ChangePasswordRequest request, AuthCallback<Void> callback) {
        Call<ApiResponse<Void>> call = authApiService.changePassword("", request); // Empty string, interceptor will inject token
        
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
     * Get user profile - No manual token needed! AuthInterceptor handles it
     */
    public void getProfile(AuthCallback<User> callback) {
        Call<ApiResponse<User>> call = authApiService.getProfile(""); // Empty string, interceptor will inject token
        
        call.enqueue(new Callback<ApiResponse<User>>() {
            @Override
            public void onResponse(Call<ApiResponse<User>> call, Response<ApiResponse<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<User> apiResponse = response.body();
                    if (apiResponse.isSucceeded()) {
                        callback.onSuccess(apiResponse.getData());
                    } else {
                        callback.onError(apiResponse.getFirstErrorMessage());
                    }
                } else {
                    callback.onError("Failed to get profile. Please try again.");
                }
            }
            
            @Override
            public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }
    
    /**
     * Update profile - No manual token needed! AuthInterceptor handles it
     */
//    public void updateProfile(UpdateProfileRequest request, AuthCallback<User> callback) {
//        Call<ApiResponse<User>> call = authApiService.updateProfile("", request); // Empty string, interceptor will inject token
//
//        call.enqueue(new Callback<ApiResponse<User>>() {
//            @Override
//            public void onResponse(Call<ApiResponse<User>> call, Response<ApiResponse<User>> response) {
//                if (response.isSuccessful() && response.body() != null) {
//                    ApiResponse<User> apiResponse = response.body();
//                    if (apiResponse.isSucceeded()) {
//                        callback.onSuccess(apiResponse.getData());
//                    } else {
//                        callback.onError(apiResponse.getFirstErrorMessage());
//                    }
//                } else {
//                    callback.onError("Failed to update profile. Please try again.");
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
//                callback.onError("Network error: " + t.getMessage());
//            }
//        });
//    }
    
    /**
     * Logout user - No manual token needed! AuthInterceptor handles it
     */
    public void logout(AuthCallback<Void> callback) {
        Call<ApiResponse<Void>> call = authApiService.logout(""); // Empty string, interceptor will inject token
        
        call.enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Void> apiResponse = response.body();
                    if (apiResponse.isSucceeded()) {
                        // Clear local session after successful server logout
                        tokenManager.clearTokens();
                        callback.onSuccess(null);
                    } else {
                        callback.onError(apiResponse.getFirstErrorMessage());
                    }
                } else {
                    // Even if server logout fails, clear local session
                    tokenManager.clearTokens();
                    callback.onError("Logout failed on server, but local session cleared.");
                }
            }
            
            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                // Clear local session even on network failure
                tokenManager.clearTokens();
                callback.onError("Network error during logout, but local session cleared.");
            }
        });
    }
    
    /**
     * Check if user is authenticated with valid token
     */
    public boolean isAuthenticated() {
        return tokenManager.isAuthenticated();
    }
    
    /**
     * Save user session after successful login/register
     */
    public void saveUserSession(AuthResponse authResponse, boolean rememberMe) {
        sessionManager.saveUserSession(authResponse, rememberMe);
    }
    
    /**
     * Get saved email for auto-fill
     */
    public String getSavedEmail() {
        return sessionManager.getSavedEmail();
    }
    
    /**
     * Check if remember me is enabled
     */
    public boolean isRememberMeEnabled() {
        return sessionManager.isRememberMeEnabled();
    }
    
    /**
     * Get current user role
     */
    public String getUserRole() {
        return sessionManager.getUserRole();
    }
    
    /**
     * Get current user email
     */
    public String getUserEmail() {
        return sessionManager.getUserEmail();
    }

    public int getUserId() {
        return sessionManager.getUserId();
    }
} 