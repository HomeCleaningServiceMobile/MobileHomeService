package com.example.prm_project.ui.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.prm_project.R;
import com.example.prm_project.databinding.ActivitySetNewPasswordBinding;
import com.example.prm_project.ui.viewmodel.AuthViewModel;
import com.google.android.material.snackbar.Snackbar;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class SetNewPasswordActivity extends AppCompatActivity {
    
    private ActivitySetNewPasswordBinding binding;
    private AuthViewModel authViewModel;
    private String resetToken;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Set up data binding
        binding = ActivitySetNewPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        // Initialize ViewModel
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        
        // Get reset token from intent (in real app, this would come from email link)
        resetToken = getIntent().getStringExtra("reset_token");
        if (resetToken == null || resetToken.isEmpty()) {
            // For demo purposes, use a placeholder token
            resetToken = "demo_reset_token";
        }
        
        // Initialize UI components
        initializeComponents();
        
        // Observe ViewModel
        observeViewModel();
    }
    
    private void initializeComponents() {
        // Back button click listener
        binding.btnBack.setOnClickListener(v -> {
            finish();
        });
        
        // Set password button click listener
        binding.btnSavePassword.setOnClickListener(v -> {
            if (validatePasswords()) {
                String newPassword = binding.etNewPassword.getText().toString().trim();
                String confirmPassword = binding.etConfirmPassword.getText().toString().trim();
                
                authViewModel.resetPassword(resetToken, newPassword, confirmPassword);
            }
        });
    }
    
    private boolean validatePasswords() {
        String newPassword = binding.etNewPassword.getText().toString().trim();
        String confirmPassword = binding.etConfirmPassword.getText().toString().trim();
        
        // Clear previous errors
        binding.tilNewPassword.setError(null);
        binding.tilConfirmPassword.setError(null);
        
        // Validate new password
        if (newPassword.isEmpty()) {
            binding.tilNewPassword.setError(getString(R.string.password_required));
            binding.etNewPassword.requestFocus();
            return false;
        }
        
        if (newPassword.length() < 6) {
            binding.tilNewPassword.setError(getString(R.string.password_too_short));
            binding.etNewPassword.requestFocus();
            return false;
        }
        
        // Validate confirm password
        if (confirmPassword.isEmpty()) {
            binding.tilConfirmPassword.setError(getString(R.string.confirm_password_required));
            binding.etConfirmPassword.requestFocus();
            return false;
        }
        
        // Check if passwords match
        if (!newPassword.equals(confirmPassword)) {
            binding.tilConfirmPassword.setError(getString(R.string.passwords_do_not_match));
            binding.etConfirmPassword.requestFocus();
            return false;
        }
        
        return true;
    }
    
    private void observeViewModel() {
        // Observe loading state
        authViewModel.getIsLoading().observe(this, isLoading -> {
            if (isLoading) {
                binding.btnSavePassword.setEnabled(false);
                binding.btnSavePassword.setText(getString(R.string.saving_password));
                // You can add a progress indicator here
            } else {
                binding.btnSavePassword.setEnabled(true);
                binding.btnSavePassword.setText(getString(R.string.save_password));
            }
        });
        
        // Observe error messages
        authViewModel.getErrorMessage().observe(this, errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                Snackbar.make(binding.getRoot(), errorMessage, Snackbar.LENGTH_LONG).show();
                authViewModel.clearMessages();
            }
        });
        
        // Observe success messages
        authViewModel.getSuccessMessage().observe(this, successMessage -> {
            if (successMessage != null && !successMessage.isEmpty()) {
                Toast.makeText(this, successMessage, Toast.LENGTH_SHORT).show();
                authViewModel.clearMessages();
            }
        });
        
        // Observe reset password success
        authViewModel.getResetPasswordSuccess().observe(this, success -> {
            if (success != null && success) {
                // Navigate back to login
                Intent intent = new Intent(SetNewPasswordActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
                
                Toast.makeText(this, getString(R.string.password_reset_success_message), Toast.LENGTH_LONG).show();
            }
        });
        

    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
} 