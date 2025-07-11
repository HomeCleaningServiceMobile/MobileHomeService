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
        // TODO: Add logout functionality to toolbar/profile section
        // For now, logout functionality is commented out as btnLogout doesn't exist in layout
        // binding.btnLogout.setOnClickListener(v -> {
        //     authViewModel.logout();
        // });
        
        // Service category buttons
        binding.cardHouseShifting.setOnClickListener(v -> {
            showToast("House Shifting Service - Coming Soon!");
        });
        
        binding.cardOfficeShifting.setOnClickListener(v -> {
            showToast("Office Shifting Service - Coming Soon!");
        });
        
        binding.cardCommercialShifting.setOnClickListener(v -> {
            showToast("Commercial Shifting Service - Coming Soon!");
        });
        
        // Chip selection (Material Design tabs)
        binding.chipTrending.setOnClickListener(v -> {
            showToast("Trending offers loaded!");
        });
        
        binding.chipPromotion.setOnClickListener(v -> {
            showToast("Promotion offers loaded!");
        });
        
        binding.chipSummerOffer.setOnClickListener(v -> {
            showToast("Summer offers loaded!");
        });
        
        binding.chipNew.setOnClickListener(v -> {
            showToast("New offers loaded!");
        });
        
        // Promotional cards
        binding.cardShiftyOffer.setOnClickListener(v -> {
            showToast("40% OFF on First Cleaning Service!");
        });
        
        binding.cardSecondOffer.setOnClickListener(v -> {
            showToast("15% OFF on Online Booking!");
        });
        
        // Other services
        binding.serviceCleaning.setOnClickListener(v -> {
            showToast("Cleaning Service - Coming Soon!");
        });
        
        binding.serviceLabour.setOnClickListener(v -> {
            showToast("Labour Service - Coming Soon!");
        });
        
        binding.serviceVehicle.setOnClickListener(v -> {
            showToast("Vehicle Service - Coming Soon!");
        });
        
        binding.servicePaint.setOnClickListener(v -> {
            showToast("Paint Service - Coming Soon!");
        });
    }
    
    private void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
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
                // Try to find the correct navigation action based on current destination
                try {
                    if (navController.getCurrentDestination().getId() == R.id.nav_home) {
                        navController.navigate(R.id.action_nav_home_to_loginFragment);
                    } else if (navController.getCurrentDestination().getId() == R.id.nav_bookings) {
                        navController.navigate(R.id.action_nav_bookings_to_loginFragment);
                    } else if (navController.getCurrentDestination().getId() == R.id.nav_services) {
                        navController.navigate(R.id.action_nav_services_to_loginFragment);
                    } else if (navController.getCurrentDestination().getId() == R.id.nav_profile) {
                        navController.navigate(R.id.action_nav_profile_to_loginFragment);
                    } else {
                        // Fallback to main fragment action
                        navController.navigate(R.id.action_mainFragment_to_loginFragment);
                    }
                } catch (Exception e) {
                    // Fallback navigation
                    navController.navigate(R.id.loginFragment);
                }
            }
        });
    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
} 