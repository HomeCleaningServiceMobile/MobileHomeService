package com.example.prm_project.ui.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.example.prm_project.R;
import com.example.prm_project.databinding.ActivityLoginBinding;
import com.example.prm_project.ui.viewmodel.AuthViewModel;
import com.google.android.material.snackbar.Snackbar;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class LoginActivity extends AppCompatActivity {
    
    private ActivityLoginBinding binding;
    private AuthViewModel authViewModel;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Set up data binding
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        // Initialize ViewModel
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        
        // Check if user is already logged in
        if (authViewModel.isUserLoggedIn()) {
            navigateToMainActivity();
            return;
        }
        
        // Initialize UI components
        initializeComponents();
        
        // Observe ViewModel
        observeViewModel();
        
        // Pre-fill email if remembered
        prefillEmailIfRemembered();
    }
    
    private void initializeComponents() {
        // Login button click listener
        binding.btnLogin.setOnClickListener(v -> {
            if (validateLoginInput()) {
                String email = binding.etEmail.getText().toString().trim();
                String password = binding.etPassword.getText().toString().trim();
                boolean rememberMe = binding.cbRememberMe.isChecked();
                
                authViewModel.login(email, password, rememberMe);
            }
        });
        
        // Forgot password click listener
        binding.btnForgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });
        
        // Sign up click listener
        binding.btnSignUp.setOnClickListener(v -> {
            // TODO: Navigate to registration activity
            Toast.makeText(this, "Registration coming soon!", Toast.LENGTH_SHORT).show();
        });
        
        // Google sign-in click listener
        binding.btnGoogleSignin.setOnClickListener(v -> {
            // TODO: Implement Google sign-in
            Toast.makeText(this, "Google Sign-in coming soon!", Toast.LENGTH_SHORT).show();
        });
    }
    
    private boolean validateLoginInput() {
        String email = binding.etEmail.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();
        
        // Clear previous errors
        binding.tilEmail.setError(null);
        binding.tilPassword.setError(null);
        binding.tvErrorMessage.setVisibility(View.GONE);
        
        boolean isValid = true;
        
        // Validate email
        if (email.isEmpty()) {
            binding.tilEmail.setError(getString(R.string.email_required));
            if (isValid) binding.etEmail.requestFocus();
            isValid = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.tilEmail.setError(getString(R.string.invalid_email_format));
            if (isValid) binding.etEmail.requestFocus();
            isValid = false;
        }
        
        // Validate password
        if (password.isEmpty()) {
            binding.tilPassword.setError(getString(R.string.password_required));
            if (isValid) binding.etPassword.requestFocus();
            isValid = false;
        }
        
        return isValid;
    }
    
    private void observeViewModel() {
        // Observe loading state
        authViewModel.getIsLoading().observe(this, isLoading -> {
            if (isLoading) {
                binding.btnLogin.setEnabled(false);
                binding.btnLogin.setText(getString(R.string.logging_in));
                // You can add a progress indicator here
            } else {
                binding.btnLogin.setEnabled(true);
                binding.btnLogin.setText(getString(R.string.login));
            }
        });
        
        // Observe error messages
        authViewModel.getErrorMessage().observe(this, errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                binding.tvErrorMessage.setText(errorMessage);
                binding.tvErrorMessage.setVisibility(View.VISIBLE);
                
                // Also show in Snackbar for better UX
                Snackbar.make(binding.getRoot(), errorMessage, Snackbar.LENGTH_LONG).show();
                
                // Clear error after showing
                authViewModel.clearMessages();
            } else {
                binding.tvErrorMessage.setVisibility(View.GONE);
            }
        });
        
        // Observe success messages
        authViewModel.getSuccessMessage().observe(this, successMessage -> {
            if (successMessage != null && !successMessage.isEmpty()) {
                Toast.makeText(this, successMessage, Toast.LENGTH_SHORT).show();
                authViewModel.clearMessages();
            }
        });
        
        // Observe login success
        authViewModel.getLoginSuccess().observe(this, loginSuccess -> {
            if (loginSuccess != null && loginSuccess) {
                navigateToMainActivity();
            }
        });
    }
    
    private void prefillEmailIfRemembered() {
        if (authViewModel.isRememberMeEnabled()) {
            String savedEmail = authViewModel.getSavedEmail();
            if (!savedEmail.isEmpty()) {
                binding.etEmail.setText(savedEmail);
                binding.cbRememberMe.setChecked(true);
                // Focus on password field
                binding.etPassword.requestFocus();
            }
        }
    }
    
    private void navigateToMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
} 