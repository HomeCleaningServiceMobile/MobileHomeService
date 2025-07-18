package com.example.prm_project.ui.view.auth;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.prm_project.R;
import com.example.prm_project.databinding.FragmentLoginBinding;
import com.example.prm_project.ui.viewmodel.AuthViewModel;
import com.google.android.material.snackbar.Snackbar;
import dagger.hilt.android.AndroidEntryPoint;
import android.util.Log;

@AndroidEntryPoint
public class LoginFragment extends Fragment {
    
    private FragmentLoginBinding binding;
    private AuthViewModel authViewModel;
    
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // Initialize ViewModel - Hilt provides all dependencies automatically
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        
        // Check if user is already logged in
        if (authViewModel.isUserLoggedIn()) {
            navigateBasedOnRole();
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
            NavController navController = Navigation.findNavController(v);
            navController.navigate(R.id.action_loginFragment_to_forgotPasswordFragment);
        });
        
        // Sign up click listener
        binding.btnSignUp.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(v);
            navController.navigate(R.id.action_loginFragment_to_registerPersonalInfoFragment);
        });
        
        // Google sign-in click listener
        binding.btnGoogleSignin.setOnClickListener(v -> {
            // TODO: Implement Google sign-in
            Toast.makeText(getContext(), "Google Sign-in coming soon!", Toast.LENGTH_SHORT).show();
        });
    }
    
    private boolean validateLoginInput() {
        String email = binding.etEmail.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();
        
        // Clear previous errors
        binding.tilEmail.setError(null);
        binding.tilPassword.setError(null);
        
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
        authViewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading) {
                binding.btnLogin.setEnabled(false);
                binding.btnLogin.setText(getString(R.string.logging_in));
            } else {
                binding.btnLogin.setEnabled(true);
                binding.btnLogin.setText(getString(R.string.login));
            }
        });
        
        // Observe error messages
        authViewModel.getErrorMessage().observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                Snackbar.make(binding.getRoot(), errorMessage, Snackbar.LENGTH_LONG).show();
                authViewModel.clearMessages();
            }
        });
        
        // Observe success messages
        authViewModel.getSuccessMessage().observe(getViewLifecycleOwner(), successMessage -> {
            if (successMessage != null && !successMessage.isEmpty()) {
                Toast.makeText(getContext(), successMessage, Toast.LENGTH_SHORT).show();
                authViewModel.clearMessages();
            }
        });
        
        // Observe login success
        authViewModel.getLoginSuccess().observe(getViewLifecycleOwner(), loginSuccess -> {
            if (loginSuccess != null && loginSuccess) {
                navigateBasedOnRole();
            }
        });
    }
    
    private void prefillEmailIfRemembered() {
        if (authViewModel.isRememberMeEnabled()) {
            String savedEmail = authViewModel.getSavedEmail();
            if (!savedEmail.isEmpty()) {
                binding.etEmail.setText(savedEmail);
                binding.cbRememberMe.setChecked(true);
                binding.etPassword.requestFocus();
            }
        }
    }
    
    private void navigateBasedOnRole() {
        NavController navController = Navigation.findNavController(requireView());
        
        // Debug logging to see what role is detected
        String userRole = authViewModel.getUserRole();
        boolean isAdmin = authViewModel.isAdmin();
        boolean isStaff = authViewModel.isStaff();
        boolean isCustomer = authViewModel.isCustomer();
        
        Log.d("LoginFragment", "User Role: " + userRole);
        Log.d("LoginFragment", "isAdmin: " + isAdmin);
        Log.d("LoginFragment", "isStaff: " + isStaff);
        Log.d("LoginFragment", "isCustomer: " + isCustomer);
        
        // TEMPORARY: Force staff navigation for testing if email contains "staff"
//        String userEmail = authViewModel.getUserEmail();
//        if (userEmail != null && userEmail.toLowerCase().contains("staff")) {
//            Log.d("LoginFragment", "TEMP: Forcing staff navigation based on email: " + userEmail);
//            navController.navigate(R.id.action_loginFragment_to_staffFragment);
//            return;
//        }
//
        // Check user role and navigate to appropriate dashboard
        if (authViewModel.isAdmin()) {
            Log.d("LoginFragment", "Navigating to Admin Fragment");
            navController.navigate(R.id.action_loginFragment_to_adminFragment);
        } else if (authViewModel.isStaff()) {
            Log.d("LoginFragment", "Navigating to Staff Fragment");
            navController.navigate(R.id.action_loginFragment_to_staffFragment);
        } else {
            // Default to customer dashboard (includes customer role or any other role)
            Log.d("LoginFragment", "Navigating to Customer Main Fragment");
            navController.navigate(R.id.action_loginFragment_to_mainFragment);
        }
    }
    
    private void navigateToMain() {
        NavController navController = Navigation.findNavController(requireView());
        navController.navigate(R.id.action_loginFragment_to_mainFragment);
    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
} 