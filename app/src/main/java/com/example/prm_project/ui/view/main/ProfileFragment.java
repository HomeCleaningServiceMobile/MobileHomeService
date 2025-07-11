package com.example.prm_project.ui.view.main;

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
import com.example.prm_project.databinding.FragmentProfileBinding;
import com.example.prm_project.ui.viewmodel.AuthViewModel;
import com.example.prm_project.ui.viewmodel.MainViewModel;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ProfileFragment extends Fragment {
    
    private FragmentProfileBinding binding;
    private AuthViewModel authViewModel;
    private MainViewModel mainViewModel;
    
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // Initialize ViewModels
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        
        // Initialize UI components
        initializeComponents();
        
        // Observe ViewModels
        observeViewModels();
        
        // Load user profile data
        loadUserProfile();
    }
    
    private void initializeComponents() {
        // Logout button functionality
        binding.btnLogout.setOnClickListener(v -> {
            authViewModel.logout();
        });
        
        // Edit profile button
        binding.btnEditProfile.setOnClickListener(v -> {
            showToast("Edit Profile - Coming Soon!");
        });
        
        // Change password button
        binding.btnChangePassword.setOnClickListener(v -> {
            showToast("Change Password - Coming Soon!");
        });
        
        // Settings button
        binding.btnSettings.setOnClickListener(v -> {
            showToast("Settings - Coming Soon!");
        });
        
        // Help & Support button
        binding.btnHelpSupport.setOnClickListener(v -> {
            showToast("Help & Support - Coming Soon!");
        });
    }
    
    private void loadUserProfile() {
        // TODO: Load actual user data from SharedPreferences or API
        // For now, showing placeholder data
        binding.tvUserName.setText("John Doe");
        binding.tvUserEmail.setText("john.doe@email.com");
        binding.tvUserPhone.setText("+1 234 567 8900");
        binding.tvMemberSince.setText("Member since Jan 2024");
    }
    
    private void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
    
    private void observeViewModels() {
        // Observe auth view model for logout
        authViewModel.getSuccessMessage().observe(getViewLifecycleOwner(), successMessage -> {
            if (successMessage != null && !successMessage.isEmpty() && successMessage.contains("Logged out")) {
                showToast("Logged out successfully!");
                // Navigate back to login
                NavController navController = Navigation.findNavController(requireView());
                navController.navigate(R.id.action_nav_profile_to_loginFragment);
            }
        });
        
        // Observe for any error messages
        authViewModel.getErrorMessage().observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                showToast("Error: " + errorMessage);
            }
        });
    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
} 