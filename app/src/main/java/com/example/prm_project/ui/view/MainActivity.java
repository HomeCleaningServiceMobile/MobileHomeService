package com.example.prm_project.ui.view;

import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.example.prm_project.R;
import com.example.prm_project.databinding.ActivityMainBinding;
import com.example.prm_project.ui.viewmodel.AuthViewModel;
import dagger.hilt.android.AndroidEntryPoint;

/**
 * Single Activity for the entire app using Navigation Component
 * All screens are implemented as Fragments
 */
@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {
    
    private ActivityMainBinding binding;
    private NavController navController;
    private AuthViewModel authViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Enable edge-to-edge display
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        
        // Initialize data binding
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        // Initialize ViewModel
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        
        // Set up Navigation Component
        setupNavigation();
        
        // Set up Bottom Navigation
        setupBottomNavigation();
        
        // Handle role-based navigation if user is already logged in
        handleInitialNavigation();
        
        // Listen for navigation changes to show/hide bottom nav
        observeNavigation();
    }
    
    private void setupNavigation() {
        // Get NavHostFragment and NavController
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        
        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();
        }
    }
    
    private void setupBottomNavigation() {
        if (navController != null) {
            // Connect bottom navigation with navigation controller
            NavigationUI.setupWithNavController(binding.bottomNavigation, navController);
        }
    }
    
    private void observeNavigation() {
        if (navController != null) {
            navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
                // Show toolbar and bottom navigation only on main app screens
                if (destination.getId() == R.id.mainFragment ||
                    destination.getId() == R.id.nav_home ||
                    destination.getId() == R.id.nav_bookings ||
                    destination.getId() == R.id.nav_services ||
                    destination.getId() == R.id.nav_profile ||
                    destination.getId() == R.id.adminFragment ||
                    destination.getId() == R.id.staffFragment) {
                    binding.topAppBar.setVisibility(View.VISIBLE);
                    binding.bottomNavigation.setVisibility(View.VISIBLE);
                    // Set rounded background for main content
                    binding.navHostFragment.setBackgroundResource(R.drawable.rounded_top_background);
                } else {
                    // Hide toolbar and bottom navigation on login/register screens
                    binding.topAppBar.setVisibility(View.GONE);
                    binding.bottomNavigation.setVisibility(View.GONE);
                    // Remove background for full screen auth views
                    binding.navHostFragment.setBackground(null);
                }
            });
        }
    }
    
    private void handleInitialNavigation() {
        // If user is already logged in, navigate to appropriate dashboard
        if (authViewModel.isUserLoggedIn()) {
            if (authViewModel.isAdmin()) {
                navController.navigate(R.id.adminFragment);
            } else if (authViewModel.isStaff()) {
                navController.navigate(R.id.staffFragment);
            } else {
                // Default to customer dashboard (home tab)
                navController.navigate(R.id.nav_home);
            }
        }
        // If not logged in, navigation will stay at loginFragment (default start destination)
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
} 