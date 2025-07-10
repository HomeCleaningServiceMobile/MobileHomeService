package com.example.prm_project.ui.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.example.prm_project.R;
import com.example.prm_project.databinding.ActivityForgotPasswordBinding;
import com.example.prm_project.ui.viewmodel.AuthViewModel;
import com.google.android.material.snackbar.Snackbar;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ForgotPasswordActivity extends AppCompatActivity {
    
    private ActivityForgotPasswordBinding binding;
    private AuthViewModel authViewModel;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Set up data binding
        binding = ActivityForgotPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        // Initialize ViewModel
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        
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
        
        // Reset password button click listener
        binding.btnResetPassword.setOnClickListener(v -> {
            if (validateEmail()) {
                String email = binding.etEmail.getText().toString().trim();
                authViewModel.forgotPassword(email);
            }
        });
    }
    
    private boolean validateEmail() {
        String email = binding.etEmail.getText().toString().trim();
        
        // Clear previous error
        binding.tilEmail.setError(null);
        
        if (email.isEmpty()) {
            binding.tilEmail.setError(getString(R.string.email_required));
            binding.etEmail.requestFocus();
            return false;
        }
        
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.tilEmail.setError(getString(R.string.invalid_email_format));
            binding.etEmail.requestFocus();
            return false;
        }
        
        return true;
    }
    
    private void observeViewModel() {
        // Observe loading state
        authViewModel.getIsLoading().observe(this, isLoading -> {
            if (isLoading) {
                binding.btnResetPassword.setEnabled(false);
                binding.btnResetPassword.setText(getString(R.string.sending_reset_email));
                // You can add a progress indicator here
            } else {
                binding.btnResetPassword.setEnabled(true);
                binding.btnResetPassword.setText(getString(R.string.reset_password));
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
        
        // Observe forgot password success
        authViewModel.getForgotPasswordSuccess().observe(this, success -> {
            if (success != null && success) {
                // Navigate to reset email sent activity
                Intent intent = new Intent(ForgotPasswordActivity.this, ResetEmailSentActivity.class);
                intent.putExtra("email", binding.etEmail.getText().toString().trim());
                startActivity(intent);
                finish();
            }
        });
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
} 