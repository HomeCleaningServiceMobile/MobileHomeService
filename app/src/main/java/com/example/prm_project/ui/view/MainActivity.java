package com.example.prm_project.ui.view;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

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
        
        // Initialize data binding
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        // Initialize ViewModel
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        
        // Set up Navigation Component
        setupNavigation();
        
        // Handle role-based navigation if user is already logged in
        handleInitialNavigation();
    }
    
    private void setupNavigation() {
        // Get NavHostFragment and NavController
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        
        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();
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
                // Default to customer dashboard
                navController.navigate(R.id.mainFragment);
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