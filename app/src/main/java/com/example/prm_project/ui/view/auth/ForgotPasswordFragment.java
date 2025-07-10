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
import com.example.prm_project.databinding.FragmentForgotPasswordBinding;
import com.example.prm_project.ui.viewmodel.AuthViewModel;
import com.google.android.material.snackbar.Snackbar;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ForgotPasswordFragment extends Fragment {
    
    private FragmentForgotPasswordBinding binding;
    private AuthViewModel authViewModel;
    
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentForgotPasswordBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // Initialize ViewModel - Hilt provides all dependencies automatically
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        
        // Initialize UI components
        initializeComponents();
        
        // Observe ViewModel
        observeViewModel();
    }
    
    private void initializeComponents() {
        // Back button click listener
        binding.btnBack.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(v);
            navController.navigateUp();
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
        authViewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading) {
                binding.btnResetPassword.setEnabled(false);
                binding.btnResetPassword.setText(getString(R.string.sending_reset_email));
            } else {
                binding.btnResetPassword.setEnabled(true);
                binding.btnResetPassword.setText(getString(R.string.reset_password));
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
        
        // Observe forgot password success
        authViewModel.getForgotPasswordSuccess().observe(getViewLifecycleOwner(), success -> {
            if (success != null && success) {
                // Navigate to reset email sent fragment
                Bundle args = new Bundle();
                args.putString("email", binding.etEmail.getText().toString().trim());
                
                NavController navController = Navigation.findNavController(requireView());
                navController.navigate(R.id.action_forgotPasswordFragment_to_resetEmailSentFragment, args);
            }
        });
    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
} 