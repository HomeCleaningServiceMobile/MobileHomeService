package com.example.prm_project.data.remote;

import com.example.prm_project.data.model.*;
import android.os.Handler;
import android.os.Looper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RemoteDataSource {
    
    private ApiService apiService;
    private AuthApiService authApiService;
    private Handler mainHandler;
    
    public RemoteDataSource() {
        // Initialize Retrofit API services
        apiService = RetrofitClient.getInstance().create(ApiService.class);
        authApiService = RetrofitClient.getInstance().create(AuthApiService.class);
        mainHandler = new Handler(Looper.getMainLooper());
    }
    
    // Callback interfaces
    public interface RemoteCallback {
        void onSuccess(String data);
        void onError(String error);
    }
    
    public interface UserRemoteCallback {
        void onSuccess(User user);
        void onError(String error);
    }
    
    // Authentication callback interfaces
    public interface AuthCallback {
        void onSuccess(AuthResponse authResponse);
        void onError(String error);
    }
    
    public interface ProfileCallback {
        void onSuccess(User user);
        void onError(String error);
    }
    
    public interface TokenCallback {
        void onSuccess(TokenResponse tokenResponse);
        void onError(String error);
    }
    
    public interface VoidCallback {
        void onSuccess();
        void onError(String error);
    }
    
    // Authentication methods
    public void login(LoginRequest loginRequest, AuthCallback callback) {
        Call<ApiResponse<AuthResponse>> call = authApiService.login(loginRequest);
        call.enqueue(new Callback<ApiResponse<AuthResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<AuthResponse>> call, Response<ApiResponse<AuthResponse>> response) {
                mainHandler.post(() -> {
                    if (response.isSuccessful() && response.body() != null) {
                        ApiResponse<AuthResponse> apiResponse = response.body();
                        if (apiResponse.isSucceeded()) {
                            callback.onSuccess(apiResponse.getData());
                        } else {
                            callback.onError(apiResponse.getFirstErrorMessage());
                        }
                    } else {
                        callback.onError("Login failed: " + response.code());
                    }
                });
            }
            
            @Override
            public void onFailure(Call<ApiResponse<AuthResponse>> call, Throwable t) {
                mainHandler.post(() -> callback.onError("Network error: " + t.getMessage()));
            }
        });
    }
    
    public void registerCustomer(CustomerRegistrationRequest customerRequest, AuthCallback callback) {
        Call<ApiResponse<AuthResponse>> call = authApiService.registerCustomer(customerRequest);
        call.enqueue(new Callback<ApiResponse<AuthResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<AuthResponse>> call, Response<ApiResponse<AuthResponse>> response) {
                mainHandler.post(() -> {
                    if (response.isSuccessful() && response.body() != null) {
                        ApiResponse<AuthResponse> apiResponse = response.body();
                        if (apiResponse.isSucceeded()) {
                            callback.onSuccess(apiResponse.getData());
                        } else {
                            callback.onError(apiResponse.getFirstErrorMessage());
                        }
                    } else {
                        callback.onError("Registration failed: " + response.code());
                    }
                });
            }
            
            @Override
            public void onFailure(Call<ApiResponse<AuthResponse>> call, Throwable t) {
                mainHandler.post(() -> callback.onError("Network error: " + t.getMessage()));
            }
        });
    }
    
    public void forgotPassword(ForgotPasswordRequest forgotPasswordRequest, VoidCallback callback) {
        Call<ApiResponse<Void>> call = authApiService.forgotPassword(forgotPasswordRequest);
        call.enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                mainHandler.post(() -> {
                    if (response.isSuccessful() && response.body() != null) {
                        ApiResponse<Void> apiResponse = response.body();
                        if (apiResponse.isSucceeded()) {
                            callback.onSuccess();
                        } else {
                            callback.onError(apiResponse.getFirstErrorMessage());
                        }
                    } else {
                        callback.onError("Forgot password request failed: " + response.code());
                    }
                });
            }
            
            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                mainHandler.post(() -> callback.onError("Network error: " + t.getMessage()));
            }
        });
    }
    
    public void resetPassword(ResetPasswordRequest resetPasswordRequest, VoidCallback callback) {
        Call<ApiResponse<Void>> call = authApiService.resetPassword(resetPasswordRequest);
        call.enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                mainHandler.post(() -> {
                    if (response.isSuccessful() && response.body() != null) {
                        ApiResponse<Void> apiResponse = response.body();
                        if (apiResponse.isSucceeded()) {
                            callback.onSuccess();
                        } else {
                            callback.onError(apiResponse.getFirstErrorMessage());
                        }
                    } else {
                        callback.onError("Password reset failed: " + response.code());
                    }
                });
            }
            
            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                mainHandler.post(() -> callback.onError("Network error: " + t.getMessage()));
            }
        });
    }
    
    public void changePassword(String authToken, ChangePasswordRequest changePasswordRequest, VoidCallback callback) {
        Call<ApiResponse<Void>> call = authApiService.changePassword("Bearer " + authToken, changePasswordRequest);
        call.enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                mainHandler.post(() -> {
                    if (response.isSuccessful() && response.body() != null) {
                        ApiResponse<Void> apiResponse = response.body();
                        if (apiResponse.isSucceeded()) {
                            callback.onSuccess();
                        } else {
                            callback.onError(apiResponse.getFirstErrorMessage());
                        }
                    } else {
                        callback.onError("Password change failed: " + response.code());
                    }
                });
            }
            
            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                mainHandler.post(() -> callback.onError("Network error: " + t.getMessage()));
            }
        });
    }

    public void getProfile(ProfileCallback callback) {
        Call<AppResponse<UserResponse>> call = authApiService.getProfile();

        call.enqueue(new Callback<AppResponse<UserResponse>>() {
            @Override
            public void onResponse(Call<AppResponse<UserResponse>> call,
                                   Response<AppResponse<UserResponse>> response) {
                mainHandler.post(() -> {
                    if (response.isSuccessful() && response.body() != null) {
                        AppResponse<UserResponse> apiResponse = response.body();
                        if (apiResponse.isSucceeded()) {
                            // ✅ Convert UserResponse -> User (if needed)
                            User user = convertUserResponseToUser(apiResponse.getData());
                            callback.onSuccess(user);
                        } else {
                            callback.onError(apiResponse.getFirstMessage("Error"));
                        }
                    } else {
                        callback.onError("Failed to get profile: " + response.code());
                    }
                });
            }

            @Override
            public void onFailure(Call<AppResponse<UserResponse>> call, Throwable t) {
                mainHandler.post(() -> callback.onError("Network error: " + t.getMessage()));
            }
        });
    }

    private User convertUserResponseToUser(UserResponse userResponse) {
        User user = new User();
        user.setId(userResponse.getId());
        user.setFullName(userResponse.getFullName());
        user.setEmail(userResponse.getEmail());
        user.setCreatedAt(userResponse.getCreatedAt());
        return user;
    }


//    public void updateProfile(String authToken, UpdateProfileRequest updateProfileRequest, ProfileCallback callback) {
//        Call<ApiResponse<User>> call = authApiService.updateProfile("Bearer " + authToken, updateProfileRequest);
//        call.enqueue(new Callback<ApiResponse<User>>() {
//            @Override
//            public void onResponse(Call<ApiResponse<User>> call, Response<ApiResponse<User>> response) {
//                mainHandler.post(() -> {
//                    if (response.isSuccessful() && response.body() != null) {
//                        ApiResponse<User> apiResponse = response.body();
//                        if (apiResponse.isSucceeded()) {
//                            callback.onSuccess(apiResponse.getData());
//                        } else {
//                            callback.onError(apiResponse.getFirstErrorMessage());
//                        }
//                    } else {
//                        callback.onError("Failed to update profile: " + response.code());
//                    }
//                });
//            }
//
//            @Override
//            public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
//                mainHandler.post(() -> callback.onError("Network error: " + t.getMessage()));
//            }
//        });
//    }
    
    public void refreshToken(RefreshTokenRequest refreshTokenRequest, TokenCallback callback) {
        Call<ApiResponse<TokenResponse>> call = authApiService.refreshToken(refreshTokenRequest);
        call.enqueue(new Callback<ApiResponse<TokenResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<TokenResponse>> call, Response<ApiResponse<TokenResponse>> response) {
                mainHandler.post(() -> {
                    if (response.isSuccessful() && response.body() != null) {
                        ApiResponse<TokenResponse> apiResponse = response.body();
                        if (apiResponse.isSucceeded()) {
                            callback.onSuccess(apiResponse.getData());
                        } else {
                            callback.onError(apiResponse.getFirstErrorMessage());
                        }
                    } else {
                        callback.onError("Failed to refresh token: " + response.code());
                    }
                });
            }
            
            @Override
            public void onFailure(Call<ApiResponse<TokenResponse>> call, Throwable t) {
                mainHandler.post(() -> callback.onError("Network error: " + t.getMessage()));
            }
        });
    }
    
    public void logout(String authToken, VoidCallback callback) {
        Call<ApiResponse<Void>> call = authApiService.logout("Bearer " + authToken);
        call.enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                mainHandler.post(() -> {
                    if (response.isSuccessful() && response.body() != null) {
                        ApiResponse<Void> apiResponse = response.body();
                        if (apiResponse.isSucceeded()) {
                            callback.onSuccess();
                        } else {
                            callback.onError(apiResponse.getFirstErrorMessage());
                        }
                    } else {
                        callback.onError("Logout failed: " + response.code());
                    }
                });
            }
            
            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                mainHandler.post(() -> callback.onError("Network error: " + t.getMessage()));
            }
        });
    }
    
    public void fetchData(RemoteCallback callback) {
        // Simulate network call
        new Thread(() -> {
            try {
                // Simulate network delay
                Thread.sleep(2000);
                
                // Simulate successful response
                String data = "Remote: Data fetched from API successfully!";
                
                // Post result back to main thread
                mainHandler.post(() -> callback.onSuccess(data));
                
            } catch (InterruptedException e) {
                mainHandler.post(() -> callback.onError("Network error: " + e.getMessage()));
            }
        }).start();
    }
    
    public void fetchUser(int userId, UserRemoteCallback callback) {
        // Simulate network call
        new Thread(() -> {
            try {
                // Simulate network delay
                Thread.sleep(1500);

                // Simulate successful response
                User user = new User();

                user.setCreatedAt("2024-01-01T00:00:00Z");

                // Post result back to main thread
                mainHandler.post(() -> callback.onSuccess(user));

            } catch (InterruptedException e) {
                mainHandler.post(() -> callback.onError("Network error: " + e.getMessage()));
            }
        }).start();
    }
    
    public void postUser(User user, UserRemoteCallback callback) {
        // Simulate POST request
        new Thread(() -> {
            try {
                Thread.sleep(1000);
                
                // Simulate successful creation
                user.setId(System.currentTimeMillis() > 0 ? 
                          (int)(System.currentTimeMillis() % 1000) : 1);
                user.setCreatedAt("2024-01-01T00:00:00Z");
                
                mainHandler.post(() -> callback.onSuccess(user));
                
            } catch (InterruptedException e) {
                mainHandler.post(() -> callback.onError("Failed to create user: " + e.getMessage()));
            }
        }).start();
    }
    
    public void updateUser(User user, UserRemoteCallback callback) {
        // Simulate PUT request
        new Thread(() -> {
            try {
                Thread.sleep(1000);
                
                // Simulate successful update
                mainHandler.post(() -> callback.onSuccess(user));
                
            } catch (InterruptedException e) {
                mainHandler.post(() -> callback.onError("Failed to update user: " + e.getMessage()));
            }
        }).start();
    }
    
    public void deleteUser(int userId, RemoteCallback callback) {
        // Simulate DELETE request
        new Thread(() -> {
            try {
                Thread.sleep(500);
                
                // Simulate successful deletion
                mainHandler.post(() -> callback.onSuccess("User deleted successfully"));
                
            } catch (InterruptedException e) {
                mainHandler.post(() -> callback.onError("Failed to delete user: " + e.getMessage()));
            }
        }).start();
    }
} 