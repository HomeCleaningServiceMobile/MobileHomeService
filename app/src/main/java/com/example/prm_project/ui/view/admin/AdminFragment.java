package com.example.prm_project.ui.view.admin;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.prm_project.R;
import com.example.prm_project.data.model.MonthlyRevenue;
import com.example.prm_project.databinding.FragmentAdminDashboardBinding;
import com.example.prm_project.ui.view.adapters.TopServiceAdapter;
import com.example.prm_project.ui.view.custom.SimpleLineChartView;
import com.example.prm_project.ui.viewmodel.AdminDashboardViewModel;
import com.example.prm_project.ui.viewmodel.AuthViewModel;

import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class AdminFragment extends Fragment {

    private static final String TAG = "AdminFragment";
    private FragmentAdminDashboardBinding binding;
    private AuthViewModel authViewModel;
    private AdminDashboardViewModel dashboardViewModel;
    private TopServiceAdapter topServiceAdapter;
    private SimpleLineChartView simpleLineChart;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView called");
        binding = FragmentAdminDashboardBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated called");

        // Initialize ViewModels
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        dashboardViewModel = new ViewModelProvider(this).get(AdminDashboardViewModel.class);

        // Initialize UI components
        initializeComponents();

        // Setup UI components
        setupUI();

        // Observe ViewModel
        observeViewModel();

        // Load data
        loadDashboardData();
    }

    private void initializeComponents() {
        // Initialize SimpleLineChart
        simpleLineChart = binding.simpleLineChartMonthlyRevenue;
        
        // Logout functionality
        binding.btnLogout.setOnClickListener(v -> {
            Log.d(TAG, "Logout button clicked");
            authViewModel.logout();
        });
    }

    private void setupUI() {
        // Setup Top Services RecyclerView
        topServiceAdapter = new TopServiceAdapter();
        binding.rvTopServices.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvTopServices.setAdapter(topServiceAdapter);
    }

    private void observeViewModel() {
        // Observe loading state
        dashboardViewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        // Observe error messages
        dashboardViewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                // Show error message (you can use Toast or Snackbar)
                android.widget.Toast.makeText(getContext(), error, android.widget.Toast.LENGTH_SHORT).show();
                dashboardViewModel.clearError();
            }
        });

        // Observe auth view model for logout
        authViewModel.getSuccessMessage().observe(getViewLifecycleOwner(), successMessage -> {
            Log.d(TAG, "AuthViewModel successMessage: " + successMessage);
            if (successMessage != null && !successMessage.isEmpty() && successMessage.contains("Logged out")) {
                Log.d(TAG, "Logout success detected, navigating to login");
                try {
                    NavController navController = Navigation.findNavController(requireView());
                    navController.navigate(R.id.action_adminFragment_to_loginFragment);
                    Log.d(TAG, "Navigation to login completed");
                } catch (Exception e) {
                    Log.e(TAG, "Navigation failed: " + e.getMessage(), e);
                }
            }
        });
    }

    private void loadDashboardData() {
        Log.d(TAG, "loadDashboardData called");
        // Setup observers first
        setupDataObservers();
        
        // Then trigger data loading
        dashboardViewModel.loadDashboardData();
    }

    private void setupDataObservers() {
        Log.d(TAG, "Setting up data observers");
        // Load User Summary
        dashboardViewModel.getUserSummary().observe(getViewLifecycleOwner(), userSummary -> {
            Log.d(TAG, "UserSummary observer triggered: " + userSummary);
            if (userSummary != null) {
                binding.tvTotalCustomers.setText(String.valueOf(userSummary.getTotalCustomers()));
                binding.tvTotalStaff.setText(String.valueOf(userSummary.getTotalStaff()));
                binding.tvActiveStaff.setText(String.valueOf(userSummary.getActiveStaff()));
            } else {
                Log.w(TAG, "UserSummary is null, setting default values");
                binding.tvTotalCustomers.setText("0");
                binding.tvTotalStaff.setText("0");
                binding.tvActiveStaff.setText("0");
            }
        });

        // Load Top Services
        dashboardViewModel.getTopServices().observe(getViewLifecycleOwner(), topServices -> {
            Log.d(TAG, "TopServices observer triggered: " + (topServices != null ? topServices.size() + " items" : "null"));
            if (topServices != null) {
                topServiceAdapter.setTopServices(topServices);
            }
        });

        // Load Monthly Revenue
        dashboardViewModel.getMonthlyRevenue().observe(getViewLifecycleOwner(), monthlyRevenues -> {
            Log.d(TAG, "MonthlyRevenue observer triggered: " + (monthlyRevenues != null ? monthlyRevenues.size() + " items" : "null"));
            if (monthlyRevenues != null) {
                updateSimpleLineChart(monthlyRevenues);
            }
        });
    }

    private void updateSimpleLineChart(List<MonthlyRevenue> monthlyRevenues) {
        if (monthlyRevenues == null || monthlyRevenues.isEmpty()) {
            Log.w(TAG, "No monthly revenue data to display");
            return;
        }
        
        simpleLineChart.setData(monthlyRevenues);
        Log.d(TAG, "SimpleLineChart updated with " + monthlyRevenues.size() + " data points");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume called - refreshing data");
        // Refresh data when fragment becomes visible
        dashboardViewModel.loadDashboardData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}