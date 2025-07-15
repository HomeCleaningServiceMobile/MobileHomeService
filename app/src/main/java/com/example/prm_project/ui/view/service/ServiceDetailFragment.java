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
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.prm_project.R;
import com.example.prm_project.data.model.Service;
import com.example.prm_project.data.model.ServicePackage;
import com.example.prm_project.ui.viewmodel.ServiceDetailViewModel;
import com.google.android.material.button.MaterialButton;
import java.util.ArrayList;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ServiceDetailFragment extends Fragment implements PackageAdapter.OnPackageClickListener {
    
    private ServiceDetailViewModel viewModel;
    private PackageAdapter packageAdapter;
    private RecyclerView packagesRecyclerView;
    private ProgressBar progressBar;
    private TextView errorText;
    private LinearLayout errorState;
    private ImageView btnBack;
    private MaterialButton btnBookService;
    private MaterialButton btnBookPackage;
    private View packagesCard;
    
    // UI Elements
    private ImageView serviceImage;
    private TextView serviceName;
    private TextView serviceDescription;
    private TextView serviceType;
    private TextView basePrice;
    private TextView serviceDuration;
    private TextView serviceHourlyRate;
    private TextView serviceRequirements;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_service_detail, container, false);
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // Initialize views
        initializeViews(view);
        
        // Setup RecyclerView
        setupRecyclerView();
        
        // Setup click listeners
        setupClickListeners();
        
        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(ServiceDetailViewModel.class);
        
        // Observe LiveData
        observeViewModel();
        
        // Load service details
        loadServiceDetails();
    }
    
    private void initializeViews(View view) {
        packagesRecyclerView = view.findViewById(R.id.packages_recycler_view);
        progressBar = view.findViewById(R.id.progress_bar);
        errorText = view.findViewById(R.id.error_text);
        errorState = view.findViewById(R.id.error_state);
        btnBack = view.findViewById(R.id.btn_back);
        btnBookService = view.findViewById(R.id.btn_book_service);
        btnBookPackage = view.findViewById(R.id.btn_book_package);
        
        // Service info views
        serviceImage = view.findViewById(R.id.service_image);
        serviceName = view.findViewById(R.id.service_name);
        serviceDescription = view.findViewById(R.id.service_description);
        serviceType = view.findViewById(R.id.service_type);
        basePrice = view.findViewById(R.id.base_price);
        serviceDuration = view.findViewById(R.id.service_duration);
        serviceHourlyRate = view.findViewById(R.id.service_hourly_rate);
        serviceRequirements = view.findViewById(R.id.service_requirements);
        
        // Find packages card to hide/show
        packagesCard = view.findViewById(R.id.packages_card);
    }
    
    private void setupRecyclerView() {
        packageAdapter = new PackageAdapter();
        packageAdapter.setOnPackageClickListener(this);
        
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
        packagesRecyclerView.setLayoutManager(layoutManager);
        packagesRecyclerView.setAdapter(packageAdapter);
    }
    
    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });
        
        btnBookService.setOnClickListener(v -> {
            navigateToBooking(false); // Book service without package
        });
        
        btnBookPackage.setOnClickListener(v -> {
            navigateToBooking(true); // Book service with package
        });
    }
    
    private void observeViewModel() {
        // Observe service details
        viewModel.getService().observe(getViewLifecycleOwner(), service -> {
            if (service != null) {
                updateServiceUI(service);
            }
        });
        
        // Observe packages
        viewModel.getPackages().observe(getViewLifecycleOwner(), packages -> {
            if (packages != null && !packages.isEmpty()) {
                // Show packages section and package button
                packagesCard.setVisibility(View.VISIBLE);
                btnBookPackage.setVisibility(View.VISIBLE);
                
                // Update adapter with new packages
                packageAdapter.setPackages(packages);
                
                // Select first package by default and update button text
                packageAdapter.setSelectedPosition(0);
                ServicePackage firstPackage = packages.get(0);
                viewModel.setSelectedPackage(firstPackage);
                btnBookPackage.setText(String.format("Book Package - $%.0f", firstPackage.getPrice()));
                
                // Log for debugging
                System.out.println("Loaded " + packages.size() + " packages for service");
                for (int i = 0; i < packages.size(); i++) {
                    ServicePackage pkg = packages.get(i);
                    System.out.println("Package " + i + ": " + pkg.getName() + " - $" + pkg.getPrice());
                }
                
                // Check if packages are loaded correctly
                checkPackagesLoaded();
                
                // Refresh display to ensure proper layout
                refreshPackagesDisplay();
            } else {
                // Hide packages section when no packages available
                packagesCard.setVisibility(View.GONE);
                btnBookPackage.setVisibility(View.GONE);
                packageAdapter.setPackages(new ArrayList<>());
                System.out.println("No packages available for this service");
            }
            
            // Always update service button text
            Service currentService = viewModel.getService().getValue();
            if (currentService != null) {
                btnBookService.setText(String.format("Book Service - From $%.0f", currentService.getBasePrice()));
            } else {
                btnBookService.setText("Book Service");
            }
        });
        
        // Observe selected package changes
        viewModel.getSelectedPackage().observe(getViewLifecycleOwner(), selectedPackage -> {
            if (selectedPackage != null) {
                btnBookPackage.setText(String.format("Book Package - $%.0f", selectedPackage.getPrice()));
                System.out.println("Selected package: " + selectedPackage.getName() + " - $" + selectedPackage.getPrice());
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
    
    private void loadServiceDetails() {
        // Get service ID from navigation arguments
        if (getArguments() != null) {
            int serviceId = getArguments().getInt("serviceId", -1);
            if (serviceId != -1) {
                viewModel.loadServiceById(serviceId);
            } else {
                showError("Invalid service ID");
            }
        } else {
            showError("No service ID provided");
        }
    }
    
    private void updateServiceUI(Service service) {
        serviceName.setText(service.getName());
        serviceDescription.setText(service.getDescription());
        serviceType.setText(viewModel.getServiceTypeText(service.getType()));
        basePrice.setText(String.format("From $%.0f", service.getBasePrice()));
        serviceDuration.setText(viewModel.formatDuration(service.getEstimatedDurationMinutes()));
        
        if (service.getHourlyRate() != null) {
            serviceHourlyRate.setText(String.format("Hourly rate: $%.0f", service.getHourlyRate()));
            serviceHourlyRate.setVisibility(View.VISIBLE);
        } else {
            serviceHourlyRate.setVisibility(View.GONE);
        }
        
        // Set service icon
        serviceImage.setImageResource(viewModel.getServiceIcon(service.getType()));
        
        // Load image using Glide if available
        if (service.getImageUrl() != null && !service.getImageUrl().isEmpty()) {
            Glide.with(this)
                    .load(service.getImageUrl())
                    .placeholder(viewModel.getServiceIcon(service.getType()))
                    .error(viewModel.getServiceIcon(service.getType()))
                    .into(serviceImage);
        }
        
        // Show requirements if available
        if (service.getRequirements() != null && !service.getRequirements().isEmpty()) {
            serviceRequirements.setText(service.getRequirements());
            serviceRequirements.setVisibility(View.VISIBLE);
        } else {
            serviceRequirements.setVisibility(View.GONE);
        }
    }
    
    private void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
        errorState.setVisibility(View.GONE);
    }
    
    private void hideLoading() {
        progressBar.setVisibility(View.GONE);
    }
    
    private void showError(String error) {
        errorText.setText(error);
        errorState.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
    }
    
    private void navigateToBooking(boolean withPackage) {
        Service currentService = viewModel.getService().getValue();
        ServicePackage selectedPackage = viewModel.getSelectedPackage().getValue();
        
        if (currentService != null) {
            // Navigate to CreateBookingFragment with pre-selected service
            Bundle args = new Bundle();
            args.putInt("serviceId", currentService.getId());
            
            // Add package ID only if booking with package
            if (withPackage && selectedPackage != null) {
                args.putInt("packageId", selectedPackage.getId());
                System.out.println("Navigating to booking with service: " + currentService.getName() + 
                                 " and package: " + selectedPackage.getName());
            } else {
                System.out.println("Navigating to booking with service only: " + currentService.getName());
            }
            
            NavController navController = Navigation.findNavController(requireView());
            navController.navigate(R.id.action_serviceDetailFragment_to_createBookingFragment, args);
        } else {
            Toast.makeText(requireContext(), "Service information not available", Toast.LENGTH_SHORT).show();
        }
    }
    
    @Override
    public void onPackageClick(ServicePackage servicePackage, int position) {
        // Log for debugging
        System.out.println("Package clicked: " + servicePackage.getName() + " at position " + position);
        
        // Update ViewModel
        viewModel.setSelectedPackage(servicePackage);
        
        // Update adapter selection
        packageAdapter.setSelectedPosition(position);
        
        // Update package button text with selected package price
        btnBookPackage.setText(String.format("Book Package - $%.0f", servicePackage.getPrice()));
        
        // Smooth scroll to selected package if needed
        scrollToSelectedPackage(position);
    }

    private void debugPackageVisibility() {
        System.out.println("=== Package Visibility Debug ===");
        System.out.println("Packages card visible: " + (packagesCard.getVisibility() == View.VISIBLE));
        System.out.println("Book package button visible: " + (btnBookPackage.getVisibility() == View.VISIBLE));
        System.out.println("RecyclerView visible: " + (packagesRecyclerView.getVisibility() == View.VISIBLE));
        System.out.println("Adapter item count: " + packageAdapter.getItemCount());
        
        List<ServicePackage> currentPackages = viewModel.getPackages().getValue();
        if (currentPackages != null) {
            System.out.println("ViewModel packages count: " + currentPackages.size());
            for (int i = 0; i < currentPackages.size(); i++) {
                ServicePackage pkg = currentPackages.get(i);
                System.out.println("Package " + i + ": " + pkg.getName() + " - $" + pkg.getPrice());
            }
        }
        System.out.println("================================");
    }

    private void checkPackagesLoaded() {
        List<ServicePackage> currentPackages = viewModel.getPackages().getValue();
        if (currentPackages != null) {
            System.out.println("=== Packages Check ===");
            System.out.println("Total packages: " + currentPackages.size());
            System.out.println("Adapter item count: " + packageAdapter.getItemCount());
            System.out.println("RecyclerView visible: " + (packagesRecyclerView.getVisibility() == View.VISIBLE));
            System.out.println("Packages card visible: " + (packagesCard.getVisibility() == View.VISIBLE));
            for (int i = 0; i < currentPackages.size(); i++) {
                ServicePackage pkg = currentPackages.get(i);
                System.out.println("Package " + i + ": " + pkg.getName() + " - $" + pkg.getPrice());
            }
            System.out.println("=====================");
        }
    }

    private void refreshPackagesDisplay() {
        // Store current scroll position
        maintainScrollPosition();
        
        // Force refresh the RecyclerView
        packageAdapter.notifyDataSetChanged();
        
        // Ensure RecyclerView is visible and properly sized
        packagesRecyclerView.post(() -> {
            packagesRecyclerView.requestLayout();
            packagesRecyclerView.invalidate();
        });
        
        // Log current state
        System.out.println("=== Refresh Packages Display ===");
        System.out.println("Adapter item count: " + packageAdapter.getItemCount());
        System.out.println("RecyclerView height: " + packagesRecyclerView.getHeight());
        System.out.println("RecyclerView visible: " + (packagesRecyclerView.getVisibility() == View.VISIBLE));
        System.out.println("Packages card visible: " + (packagesCard.getVisibility() == View.VISIBLE));
        System.out.println("================================");
    }

    private void maintainScrollPosition() {
        LinearLayoutManager layoutManager = (LinearLayoutManager) packagesRecyclerView.getLayoutManager();
        if (layoutManager != null) {
            int firstVisiblePosition = layoutManager.findFirstVisibleItemPosition();
            View firstVisibleView = layoutManager.findViewByPosition(firstVisiblePosition);
            int topOffset = 0;
            if (firstVisibleView != null) {
                topOffset = firstVisibleView.getTop();
            }
            final int finalFirstVisiblePosition = firstVisiblePosition;
            final int finalTopOffset = topOffset;
            packagesRecyclerView.post(() -> {
                layoutManager.scrollToPositionWithOffset(finalFirstVisiblePosition, finalTopOffset);
            });
        }
    }

    private void scrollToSelectedPackage(int position) {
        LinearLayoutManager layoutManager = (LinearLayoutManager) packagesRecyclerView.getLayoutManager();
        if (layoutManager != null) {
            // Check if the selected package is fully visible
            int firstVisible = layoutManager.findFirstVisibleItemPosition();
            int lastVisible = layoutManager.findLastVisibleItemPosition();
            
            // Only scroll if the selected package is not fully visible
            if (position < firstVisible || position > lastVisible) {
                packagesRecyclerView.smoothScrollToPosition(position);
            }
        }
    }
} 