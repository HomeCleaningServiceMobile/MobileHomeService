package com.example.prm_project.ui.view.service;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

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
    private LinearLayout emptyState;
    private ImageView btnBack;
    
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
        emptyState = view.findViewById(R.id.empty_state);
        btnBack = view.findViewById(R.id.btn_back);
        
        // Setup RecyclerView
        setupRecyclerView();
        
        // Setup back button
        setupBackButton();
        
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
        
        GridLayoutManager layoutManager = new GridLayoutManager(requireContext(), 2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        
        // Set item decoration for better spacing
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull android.graphics.Rect outRect, @NonNull View view, 
                                     @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.bottom = 8; // Add bottom margin between items
            }
        });
    }
    
    private void setupBackButton() {
        btnBack.setOnClickListener(v -> {
            // Navigate back or close fragment
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
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
        emptyState.setVisibility(View.GONE);
    }
    
    private void hideLoading() {
        progressBar.setVisibility(View.GONE);
    }
    
    private void showContent() {
        recyclerView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        emptyState.setVisibility(View.GONE);
    }
    
    private void showError(String error) {
        errorText.setText(error);
        emptyState.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
    }
    
    private void showEmptyState() {
        errorText.setText(getString(R.string.no_services_available));
        emptyState.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
    }
    
    @Override
    public void onServiceClick(Service service) {
        // Navigate to service detail fragment
        Bundle args = new Bundle();
        args.putInt("serviceId", service.getId());
        
        NavController navController = Navigation.findNavController(requireView());
        navController.navigate(R.id.action_nav_services_to_serviceDetailFragment, args);
    }
} 