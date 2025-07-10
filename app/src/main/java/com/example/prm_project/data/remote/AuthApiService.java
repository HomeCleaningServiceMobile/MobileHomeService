package com.example.prm_project.data.remote;

import com.example.prm_project.data.model.*;
import retrofit2.Call;
import retrofit2.http.*;

public interface AuthApiService {
    
    @POST("login")
    Call<ApiResponse<AuthResponse>> login(@Body LoginRequest loginRequest);
    
    @POST("register/customer")
    Call<ApiResponse<AuthResponse>> registerCustomer(@Body CustomerRegistrationRequest customerRequest);
    
    @POST("forgot-password")
    Call<ApiResponse<Void>> forgotPassword(@Body ForgotPasswordRequest forgotPasswordRequest);
    
    @POST("reset-password")
    Call<ApiResponse<Void>> resetPassword(@Body ResetPasswordRequest resetPasswordRequest);
    
    @POST("change-password")
    Call<ApiResponse<Void>> changePassword(
        @Header("Authorization") String authToken,
        @Body ChangePasswordRequest changePasswordRequest
    );
    
    @GET("profile")
    Call<ApiResponse<User>> getProfile(@Header("Authorization") String authToken);
    
    @PUT("profile")
    Call<ApiResponse<User>> updateProfile(
        @Header("Authorization") String authToken,
        @Body UpdateProfileRequest updateProfileRequest
    );
    
    @POST("refresh")
    Call<ApiResponse<TokenResponse>> refreshToken(@Body RefreshTokenRequest refreshTokenRequest);
    
    @POST("logout")
    Call<ApiResponse<Void>> logout(@Header("Authorization") String authToken);
} 