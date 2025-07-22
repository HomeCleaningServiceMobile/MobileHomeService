package com.example.prm_project.ui.view.admin;

import android.graphics.Color;
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
import com.example.prm_project.ui.viewmodel.AdminDashboardViewModel;
import com.example.prm_project.ui.viewmodel.AuthViewModel;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class AdminFragment extends Fragment {

    private static final String TAG = "AdminFragment";
    private FragmentAdminDashboardBinding binding;
    private AuthViewModel authViewModel;
    private AdminDashboardViewModel dashboardViewModel;
    private TopServiceAdapter topServiceAdapter;
    private LineChart lineChart;

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
        // Initialize LineChart
        lineChart = binding.lineChartMonthlyRevenue;
        setupLineChart();
        
        // Logout functionality (you can add a logout button if needed)
        // binding.btnLogout.setOnClickListener(v -> {
        //     authViewModel.logout();
        // });
    }

    private void setupUI() {
        // Setup Top Services RecyclerView
        topServiceAdapter = new TopServiceAdapter();
        binding.rvTopServices.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvTopServices.setAdapter(topServiceAdapter);
    }

    private void setupLineChart() {
        // Configure LineChart
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(false);
        lineChart.setDrawGridBackground(false);
        lineChart.setPinchZoom(false);
        lineChart.setBackgroundColor(getResources().getColor(R.color.admin_white, null));

        // Configure X-axis
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setTextColor(getResources().getColor(R.color.admin_text_secondary, null));
        xAxis.setTextSize(10f);

        // Configure Y-axis
        YAxis leftYAxis = lineChart.getAxisLeft();
        leftYAxis.setDrawGridLines(true);
        leftYAxis.setAxisMinimum(0f);
        leftYAxis.setTextColor(getResources().getColor(R.color.admin_text_secondary, null));
        leftYAxis.setTextSize(10f);

        YAxis rightYAxis = lineChart.getAxisRight();
        rightYAxis.setEnabled(false);

        // Configure legend and description
        lineChart.getLegend().setEnabled(false);
        Description description = new Description();
        description.setText("");
        lineChart.setDescription(description);
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

        // Observe auth state for logout
        authViewModel.getSuccessMessage().observe(getViewLifecycleOwner(), successMessage -> {
            if (successMessage != null && !successMessage.isEmpty() && successMessage.contains("Logged out")) {
                NavController navController = Navigation.findNavController(requireView());
                navController.navigate(R.id.action_adminFragment_to_loginFragment);
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
                updateLineChart(monthlyRevenues);
            }
        });
    }

    private void updateLineChart(List<MonthlyRevenue> monthlyRevenues) {
        if (monthlyRevenues == null || monthlyRevenues.isEmpty()) {
            Log.w(TAG, "No monthly revenue data to display");
            return;
        }

        List<Entry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();

        for (int i = 0; i < monthlyRevenues.size(); i++) {
            MonthlyRevenue revenue = monthlyRevenues.get(i);
            entries.add(new Entry(i, revenue.getRevenue()));
            
            // Format month label (convert from "2025-02" to "Feb 2025")
            String monthLabel = formatMonthLabel(revenue.getMonth());
            labels.add(monthLabel);
        }

        // Create dataset
        LineDataSet dataSet = new LineDataSet(entries, "Monthly Revenue");
        dataSet.setColor(getResources().getColor(R.color.admin_primary, null));
        dataSet.setCircleColor(getResources().getColor(R.color.admin_primary, null));
        dataSet.setLineWidth(3f);
        dataSet.setCircleRadius(6f);
        dataSet.setDrawCircleHole(false);
        dataSet.setValueTextSize(10f);
        dataSet.setValueTextColor(getResources().getColor(R.color.admin_text_primary, null));
        dataSet.setDrawFilled(true);
        dataSet.setFillColor(getResources().getColor(R.color.primary_blue_light, null));

        // Create line data
        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);

        // Set custom labels for X-axis
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setLabelCount(labels.size());

        // Refresh chart
        lineChart.invalidate();
        Log.d(TAG, "LineChart updated with " + entries.size() + " data points");
    }

    private String formatMonthLabel(String monthString) {
        try {
            // Parse "2025-02" format
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM", Locale.getDefault());
            Date date = inputFormat.parse(monthString);
            
            // Format to "Feb 25"
            SimpleDateFormat outputFormat = new SimpleDateFormat("MMM yy", Locale.getDefault());
            return outputFormat.format(date);
        } catch (Exception e) {
            Log.e(TAG, "Error formatting month label: " + monthString, e);
            return monthString; // Return original if parsing fails
        }
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