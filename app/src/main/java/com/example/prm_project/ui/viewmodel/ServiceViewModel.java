package com.example.prm_project.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.prm_project.data.repository.ServiceRepository;
import com.example.prm_project.data.model.Service;
import dagger.hilt.android.lifecycle.HiltViewModel;
import javax.inject.Inject;
import java.util.List;

@HiltViewModel
public class ServiceViewModel extends ViewModel {
    
    private ServiceRepository repository;
    private MutableLiveData<List<Service>> services = new MutableLiveData<>();
    private MutableLiveData<Service> selectedService = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();
    
    /**
     * Constructor with dependency injection
     * ServiceRepository is automatically provided by Hilt
     */
    @Inject
    public ServiceViewModel(ServiceRepository repository) {
        this.repository = repository;
        isLoading.setValue(false);
    }
    
    // Getter methods for LiveData
    public LiveData<List<Service>> getServices() {
        return services;
    }
    
    public LiveData<Service> getSelectedService() {
        return selectedService;
    }
    
    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }
    
    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }
    
    // Business logic methods
    
    /**
     * Load all services with default parameters
     */
    public void loadServices() {
        loadServices(null, true, null, null, null, 1, 10);
    }
    
    /**
     * Refresh services (same as loadServices but for pull-to-refresh)
     */
    public void refreshServices() {
        loadServices(null, true, null, null, null, 1, 10);
    }
    
    /**
     * Load services with filtering and pagination
     */
    public void loadServices(Integer type, Boolean isActive, Double minPrice, Double maxPrice, 
                           String searchTerm, Integer pageNumber, Integer pageSize) {
        isLoading.setValue(true);
        errorMessage.setValue(null);
        
        repository.getServices(type, isActive, minPrice, maxPrice, searchTerm, pageNumber, pageSize,
            new ServiceRepository.ServiceCallback<List<Service>>() {
                @Override
                public void onSuccess(List<Service> data) {
                    services.setValue(data);
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
     * Load service by ID
     */
    public void loadServiceById(int id) {
        isLoading.setValue(true);
        errorMessage.setValue(null);
        
        repository.getServiceById(id, new ServiceRepository.ServiceCallback<Service>() {
            @Override
            public void onSuccess(Service data) {
                selectedService.setValue(data);
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
     * Load services by type
     */
    public void loadServicesByType(int type) {
        isLoading.setValue(true);
        errorMessage.setValue(null);
        
        repository.getServicesByType(type, new ServiceRepository.ServiceCallback<List<Service>>() {
            @Override
            public void onSuccess(List<Service> data) {
                services.setValue(data);
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
     * Load popular services
     */
    public void loadPopularServices(int limit) {
        isLoading.setValue(true);
        errorMessage.setValue(null);
        
        repository.getPopularServices(limit, new ServiceRepository.ServiceCallback<List<Service>>() {
            @Override
            public void onSuccess(List<Service> data) {
                services.setValue(data);
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