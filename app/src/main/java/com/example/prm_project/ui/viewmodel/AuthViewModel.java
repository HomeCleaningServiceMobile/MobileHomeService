package com.example.prm_project.ui.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.prm_project.data.model.*;
import com.example.prm_project.data.repository.AuthRepository;
import dagger.hilt.android.lifecycle.HiltViewModel;
import javax.inject.Inject;

@HiltViewModel
public class AuthViewModel extends ViewModel {
    
    private AuthRepository authRepository;
    
    // LiveData for UI states
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private MutableLiveData<String> successMessage = new MutableLiveData<>();
    private MutableLiveData<Boolean> loginSuccess = new MutableLiveData<>();
    private MutableLiveData<Boolean> forgotPasswordSuccess = new MutableLiveData<>();
    private MutableLiveData<Boolean> resetPasswordSuccess = new MutableLiveData<>();
    
    /**
     * Constructor with dependency injection
     * AuthRepository is automatically provided by Hilt
     */
    @Inject
    public AuthViewModel(AuthRepository authRepository) {
        this.authRepository = authRepository;
        
        // Pre-fill email if remembered
        if (authRepository.isRememberMeEnabled()) {
            String savedEmail = authRepository.getSavedEmail();
            if (!savedEmail.isEmpty()) {
                // Email will be set in the UI
            }
        }
    }
    
    // Getters for LiveData
    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }
    
    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }
    
    public LiveData<String> getSuccessMessage() {
        return successMessage;
    }
    
    public LiveData<Boolean> getLoginSuccess() {
        return loginSuccess;
    }
    
    public LiveData<Boolean> getForgotPasswordSuccess() {
        return forgotPasswordSuccess;
    }
    
    public LiveData<Boolean> getResetPasswordSuccess() {
        return resetPasswordSuccess;
    }
    
    /**
     * Perform user login
     */
    public void login(String email, String password, boolean rememberMe) {
        if (!validateLoginInput(email, password)) {
            return;
        }
        
        isLoading.setValue(true);
        errorMessage.setValue(null);
        
        LoginRequest loginRequest = new LoginRequest(email, password);
        
        authRepository.login(loginRequest, new AuthRepository.AuthCallback<AuthResponse>() {
            @Override
            public void onSuccess(AuthResponse response) {
                isLoading.setValue(false);
                
                // Save session
                authRepository.saveUserSession(response, rememberMe);
                
                successMessage.setValue("Login successful");
                loginSuccess.setValue(true);
            }
            
            @Override
            public void onError(String error) {
                isLoading.setValue(false);
                errorMessage.setValue(error);
                loginSuccess.setValue(false);
            }
        });
    }
    
    /**
     * Request password reset
     */
    public void forgotPassword(String email) {
        if (!validateEmail(email)) {
            errorMessage.setValue("Please enter a valid email address");
            return;
        }
        
        isLoading.setValue(true);
        errorMessage.setValue(null);
        
        ForgotPasswordRequest request = new ForgotPasswordRequest(email);
        
        authRepository.forgotPassword(request, new AuthRepository.AuthCallback<Void>() {
            @Override
            public void onSuccess(Void response) {
                isLoading.setValue(false);
                successMessage.setValue("Password reset instructions sent to your email");
                forgotPasswordSuccess.setValue(true);
            }
            
            @Override
            public void onError(String error) {
                isLoading.setValue(false);
                errorMessage.setValue(error);
                forgotPasswordSuccess.setValue(false);
            }
        });
    }
    
    /**
     * Reset password with new password
     */
    public void resetPassword(String token, String newPassword, String confirmPassword) {
        if (!validatePasswordReset(newPassword, confirmPassword)) {
            return;
        }
        
        isLoading.setValue(true);
        errorMessage.setValue(null);
        
        ResetPasswordRequest request = new ResetPasswordRequest(token, newPassword);
        
        authRepository.resetPassword(request, new AuthRepository.AuthCallback<Void>() {
            @Override
            public void onSuccess(Void response) {
                isLoading.setValue(false);
                successMessage.setValue("Password reset successfully");
                resetPasswordSuccess.setValue(true);
            }
            
            @Override
            public void onError(String error) {
                isLoading.setValue(false);
                errorMessage.setValue(error);
                resetPasswordSuccess.setValue(false);
            }
        });
    }
    
    /**
     * Logout user
     */
    public void logout() {
        isLoading.setValue(true);
        
        authRepository.logout(new AuthRepository.AuthCallback<Void>() {
            @Override
            public void onSuccess(Void response) {
                isLoading.setValue(false);
                successMessage.setValue("Logged out successfully");
            }
            
            @Override
            public void onError(String error) {
                isLoading.setValue(false);
                // Session is already cleared by AuthRepository
            }
        });
    }
    
    /**
     * Check if user is logged in
     */
    public boolean isUserLoggedIn() {
        return authRepository.isAuthenticated();
    }
    
    /**
     * Get saved email for auto-fill
     */
    public String getSavedEmail() {
        return authRepository.getSavedEmail();
    }
    
    /**
     * Check if remember me is enabled
     */
    public boolean isRememberMeEnabled() {
        return authRepository.isRememberMeEnabled();
    }
    
    // Validation methods
    private boolean validateLoginInput(String email, String password) {
        if (email == null || email.trim().isEmpty()) {
            errorMessage.setValue("Email is required");
            return false;
        }
        
        if (!validateEmail(email)) {
            errorMessage.setValue("Please enter a valid email address");
            return false;
        }
        
        if (password == null || password.trim().isEmpty()) {
            errorMessage.setValue("Password is required");
            return false;
        }
        
        return true;
    }
    
    private boolean validateEmail(String email) {
        return email != null && android.util.Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches();
    }
    
    private boolean validatePasswordReset(String newPassword, String confirmPassword) {
        if (newPassword == null || newPassword.trim().isEmpty()) {
            errorMessage.setValue("New password is required");
            return false;
        }
        
        if (newPassword.length() < 8) {
            errorMessage.setValue("Password must be at least 8 characters long");
            return false;
        }
        
        if (!newPassword.equals(confirmPassword)) {
            errorMessage.setValue("Passwords do not match");
            return false;
        }
        
        // Check password strength
        if (!isPasswordStrong(newPassword)) {
            errorMessage.setValue("Password must contain uppercase, lowercase, and number");
            return false;
        }
        
        return true;
    }
    
    private boolean isPasswordStrong(String password) {
        return password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$");
    }
    
    /**
     * Clear error and success messages
     */
    public void clearMessages() {
        errorMessage.setValue(null);
        successMessage.setValue(null);
    }
} 