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
import com.example.prm_project.databinding.FragmentSetNewPasswordBinding;
import com.example.prm_project.ui.viewmodel.AuthViewModel;
import com.google.android.material.snackbar.Snackbar;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class SetNewPasswordFragment extends Fragment {
    
    private FragmentSetNewPasswordBinding binding;
    private AuthViewModel authViewModel;
    private String resetToken;
    
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSetNewPasswordBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // Initialize ViewModel - Hilt provides all dependencies automatically
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        
        // Get reset token from arguments (in real app, this would come from deep link)
        Bundle args = getArguments();
        if (args != null) {
            resetToken = args.getString("reset_token");
        }
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
            NavController navController = Navigation.findNavController(v);
            navController.navigateUp();
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
        authViewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading) {
                binding.btnSavePassword.setEnabled(false);
                binding.btnSavePassword.setText(getString(R.string.saving_password));
            } else {
                binding.btnSavePassword.setEnabled(true);
                binding.btnSavePassword.setText(getString(R.string.save_password));
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
        
        // Observe reset password success
        authViewModel.getResetPasswordSuccess().observe(getViewLifecycleOwner(), success -> {
            if (success != null && success) {
                // Navigate back to login
                NavController navController = Navigation.findNavController(requireView());
                navController.navigate(R.id.action_setNewPasswordFragment_to_loginFragment);
                
                Toast.makeText(getContext(), getString(R.string.password_reset_success_message), Toast.LENGTH_LONG).show();
            }
        });
    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
} 