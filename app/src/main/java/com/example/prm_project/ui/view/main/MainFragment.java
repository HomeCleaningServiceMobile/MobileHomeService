package com.example.prm_project.ui.view.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.prm_project.R;
import com.example.prm_project.databinding.FragmentMainBinding;
import com.example.prm_project.ui.viewmodel.AuthViewModel;
import com.example.prm_project.ui.viewmodel.MainViewModel;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainFragment extends Fragment {
    
    private FragmentMainBinding binding;
    private MainViewModel mainViewModel;
    private AuthViewModel authViewModel;
    
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMainBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // Initialize ViewModels - Hilt provides all dependencies automatically
        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        
        // Set ViewModel in binding for data binding
        binding.setViewModel(mainViewModel);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        
        // Initialize UI components
        initializeComponents();
        
        // Observe ViewModels
        observeViewModels();
    }
    
    private void initializeComponents() {
        // Add logout functionality
        binding.btnLogout.setOnClickListener(v -> {
            authViewModel.logout();
        });
        
        // Quick action buttons
        binding.btnQuickBook.setOnClickListener(v -> {
            // TODO: Navigate to quick booking
            showToast("Quick Book - Coming Soon!");
        });
        
        binding.btnMyBookings.setOnClickListener(v -> {
            // TODO: Navigate to my bookings
            showToast("My Bookings - Coming Soon!");
        });
        
        // Service category buttons
        binding.btnCleaning.setOnClickListener(v -> {
            showToast("Cleaning Service - Coming Soon!");
        });
        
        binding.btnPlumbing.setOnClickListener(v -> {
            showToast("Plumbing Service - Coming Soon!");
        });
        
        binding.btnElectrical.setOnClickListener(v -> {
            showToast("Electrical Service - Coming Soon!");
        });
        
        binding.btnCarpentry.setOnClickListener(v -> {
            showToast("Carpentry Service - Coming Soon!");
        });
        
        binding.btnBrowseAll.setOnClickListener(v -> {
            showToast("Browse All Services - Coming Soon!");
        });
    }
    
    private void showToast(String message) {
        android.widget.Toast.makeText(getContext(), message, android.widget.Toast.LENGTH_SHORT).show();
    }
    
    private void observeViewModels() {
        // Observe main view model
        mainViewModel.getMessage().observe(getViewLifecycleOwner(), message -> {
            // Handle message changes if needed
            // The binding will automatically update the UI
        });
        
        // Observe auth view model for logout
        authViewModel.getSuccessMessage().observe(getViewLifecycleOwner(), successMessage -> {
            if (successMessage != null && !successMessage.isEmpty() && successMessage.contains("Logged out")) {
                // Navigate back to login after successful logout
                NavController navController = Navigation.findNavController(requireView());
                navController.navigate(R.id.action_mainFragment_to_loginFragment);
            }
        });
    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
} 