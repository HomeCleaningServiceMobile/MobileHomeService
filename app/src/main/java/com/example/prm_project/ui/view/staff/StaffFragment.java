package com.example.prm_project.ui.view.staff;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.prm_project.R;
import com.example.prm_project.databinding.FragmentStaffBinding;
import com.example.prm_project.ui.viewmodel.AuthViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class StaffFragment extends Fragment {
    
    private FragmentStaffBinding binding;
    private AuthViewModel authViewModel;
    
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentStaffBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // Initialize ViewModel
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        
        // Initialize UI components
        initializeComponents();
        
        // Setup bottom navigation
        setupBottomNavigation();
        
        // Load default fragment
        loadFragment(new StaffDashboardFragment());
        
        // Observe ViewModel
        observeViewModel();
    }
    
    private void initializeComponents() {
        // Logout functionality
        binding.btnLogout.setOnClickListener(v -> {
            authViewModel.logout();
        });
    }
    
    private void setupBottomNavigation() {
        binding.staffBottomNavigation.setOnItemSelectedListener(item -> {
            Fragment fragment = null;
            
            if (item.getItemId() == R.id.staff_nav_dashboard) {
                fragment = new StaffDashboardFragment();
            } else if (item.getItemId() == R.id.staff_nav_assignments) {
                fragment = new StaffAssignmentsFragment();
            } else if (item.getItemId() == R.id.staff_nav_schedule) {
                fragment = new StaffScheduleFragment();
            } else if (item.getItemId() == R.id.staff_nav_profile) {
                fragment = new StaffProfileFragment();
            }
            
            if (fragment != null) {
                loadFragment(fragment);
                return true;
            }
            
            return false;
        });
    }
    
    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.staff_content_container, fragment);
        transaction.commit();
    }
    
    private void observeViewModel() {
        // Observe auth view model for logout
        authViewModel.getSuccessMessage().observe(getViewLifecycleOwner(), successMessage -> {
            if (successMessage != null && !successMessage.isEmpty() && successMessage.contains("Logged out")) {
                // Navigate back to login after successful logout
                NavController navController = Navigation.findNavController(requireView());
                navController.navigate(R.id.action_staffFragment_to_loginFragment);
            }
        });
    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
} 