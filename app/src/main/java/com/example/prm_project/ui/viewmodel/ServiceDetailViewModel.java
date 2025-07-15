package com.example.prm_project.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.prm_project.data.repository.ServiceRepository;
import com.example.prm_project.data.model.Service;
import com.example.prm_project.data.model.ServicePackage;

import dagger.hilt.android.lifecycle.HiltViewModel;
import javax.inject.Inject;
import java.util.List;
import java.util.ArrayList;

@HiltViewModel
public class ServiceDetailViewModel extends ViewModel {
    
    private ServiceRepository repository;
    private MutableLiveData<Service> service = new MutableLiveData<>();
    private MutableLiveData<List<ServicePackage>> packages = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private MutableLiveData<ServicePackage> selectedPackage = new MutableLiveData<>();
    
    @Inject
    public ServiceDetailViewModel(ServiceRepository repository) {
        this.repository = repository;
        isLoading.setValue(false);
    }
    
    // Getter methods for LiveData
    public LiveData<Service> getService() {
        return service;
    }
    
    public LiveData<List<ServicePackage>> getPackages() {
        return packages;
    }
    
    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }
    
    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }
    
    public LiveData<ServicePackage> getSelectedPackage() {
        return selectedPackage;
    }
    
    // Business logic methods
    
    /**
     * Load service details by ID
     */
    public void loadServiceById(int serviceId) {
        isLoading.setValue(true);
        errorMessage.setValue(null);
        
        repository.getServiceById(serviceId, new ServiceRepository.ServiceCallback<Service>() {
            @Override
            public void onSuccess(Service data) {
                service.setValue(data);
                loadServicePackages(serviceId);
                isLoading.setValue(false);
            }
            
            @Override
            public void onError(String error) {
                errorMessage.setValue(error);
                isLoading.setValue(false);
            }
        });
    }
    
    /**
     * Load service packages for the service
     */
    private void loadServicePackages(int serviceId) {
        repository.getServicePackages(serviceId, new ServiceRepository.ServiceCallback<List<ServicePackage>>() {
            @Override
            public void onSuccess(List<ServicePackage> data) {
                packages.setValue(data);
                // Auto-select first package if available
                if (data != null && !data.isEmpty()) {
                    selectedPackage.setValue(data.get(0));
                }
            }
            
            @Override
            public void onError(String error) {
                // Set empty list instead of showing error for packages
                packages.setValue(new ArrayList<>());
                // Don't show error for packages - service can be booked without packages
            }
        });
    }
    
    /**
     * Set selected package
     */
    public void setSelectedPackage(ServicePackage servicePackage) {
        selectedPackage.setValue(servicePackage);
    }
    
    /**
     * Get service type text
     */
    public String getServiceTypeText(int type) {
        switch (type) {
            case 1: return "House Cleaning";
            case 2: return "Cooking";
            case 3: return "Laundry";
            case 4: return "Ironing";
            case 5: return "Gardening";
            case 6: return "Babysitting";
            case 7: return "Elder Care";
            case 8: return "Pet Care";
            case 9: return "General Maintenance";
            default: return "Other";
        }
    }
    
    /**
     * Format duration for display
     */
    public String formatDuration(int minutes) {
        int hours = minutes / 60;
        int mins = minutes % 60;
        
        if (hours > 0 && mins > 0) {
            return hours + "h " + mins + "m";
        } else if (hours > 0) {
            return hours + "h";
        } else {
            return mins + "m";
        }
    }
    
    /**
     * Get service icon resource
     */
    public int getServiceIcon(int type) {
        switch (type) {
            case 1: return com.example.prm_project.R.drawable.ic_cleaning;
            case 2: return com.example.prm_project.R.drawable.ic_cooking;
            case 3: return com.example.prm_project.R.drawable.ic_laundry;
            case 4: return com.example.prm_project.R.drawable.ic_ironing;
            case 5: return com.example.prm_project.R.drawable.ic_gardening;
            case 6: return com.example.prm_project.R.drawable.ic_babysitting;
            case 7: return com.example.prm_project.R.drawable.ic_elder_care;
            case 8: return com.example.prm_project.R.drawable.ic_pet_care;
            case 9: return com.example.prm_project.R.drawable.ic_maintenance;
            default: return com.example.prm_project.R.drawable.ic_cleaning;
        }
    }
    
    /**
     * Clear error message
     */
    public void clearError() {
        errorMessage.setValue(null);
    }
    
    @Override
    protected void onCleared() {
        super.onCleared();
        // Clean up any resources
    }
} 