package com.example.prm_project.ui.view.service;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.prm_project.R;
import com.example.prm_project.data.model.Service;
import com.example.prm_project.ui.viewmodel.ServiceViewModel;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ServicesFragment extends Fragment implements ServiceAdapter.OnServiceClickListener {
    
    private ServiceViewModel viewModel;
    private ServiceAdapter adapter;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView errorText;
    private SwipeRefreshLayout swipeRefreshLayout;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_services, container, false);
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // Initialize views
        recyclerView = view.findViewById(R.id.services_recycler_view);
        progressBar = view.findViewById(R.id.progress_bar);
        errorText = view.findViewById(R.id.error_text);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        
        // Setup RecyclerView
        setupRecyclerView();
        
        // Setup SwipeRefreshLayout
        setupSwipeRefresh();
        
        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(ServiceViewModel.class);
        
        // Observe LiveData
        observeViewModel();
        
        // Load services
        loadServices();
    }
    
    private void setupRecyclerView() {
        adapter = new ServiceAdapter();
        adapter.setOnServiceClickListener(this);
        
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);
    }
    
    private void setupSwipeRefresh() {
        swipeRefreshLayout.setOnRefreshListener(() -> {
            loadServices();
        });
    }
    
    private void observeViewModel() {
        // Observe services
        viewModel.getServices().observe(getViewLifecycleOwner(), services -> {
            if (services != null && !services.isEmpty()) {
                adapter.setServices(services);
                showContent();
            } else {
                showEmptyState();
            }
        });
        
        // Observe loading state
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading) {
                showLoading();
            } else {
                hideLoading();
            }
        });
        
        // Observe error messages
        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                showError(error);
                Toast.makeText(requireContext(), error, Toast.LENGTH_LONG).show();
            }
        });
    }
    
    private void loadServices() {
        viewModel.loadServices();
    }
    
    private void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        errorText.setVisibility(View.GONE);
    }
    
    private void hideLoading() {
        progressBar.setVisibility(View.GONE);
        swipeRefreshLayout.setRefreshing(false);
    }
    
    private void showContent() {
        recyclerView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        errorText.setVisibility(View.GONE);
    }
    
    private void showError(String error) {
        errorText.setVisibility(View.VISIBLE);
        errorText.setText(error);
        recyclerView.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
    }
    
    private void showEmptyState() {
        errorText.setVisibility(View.VISIBLE);
        errorText.setText(getString(R.string.no_services_available));
        recyclerView.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
    }
    
    @Override
    public void onServiceClick(Service service) {
        // Handle service click - navigate to service detail or booking
        Toast.makeText(requireContext(), "Selected: " + service.getName(), Toast.LENGTH_SHORT).show();
        
        // TODO: Navigate to service detail or booking fragment
        // You can use Navigation component or Fragment transaction here
    }
} 