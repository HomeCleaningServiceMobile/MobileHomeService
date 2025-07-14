package com.example.prm_project.data.repository;

import com.example.prm_project.data.remote.ServiceApiService;
import com.example.prm_project.data.model.ApiResponse;
import com.example.prm_project.data.model.Service;
import com.example.prm_project.data.model.ItemsWrapper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

@Singleton
public class ServiceRepository {
    
    private ServiceApiService serviceApiService;
    
    /**
     * Constructor with dependency injection
     * ServiceApiService is automatically provided by Hilt
     */
    @Inject
    public ServiceRepository(ServiceApiService serviceApiService) {
        this.serviceApiService = serviceApiService;
    }
    
    // Callback interface for service operations
    public interface ServiceCallback<T> {
        void onSuccess(T data);
        void onError(String error);
    }
    
    /**
     * Get all services with filtering and pagination
     */
    public void getServices(Integer type, Boolean isActive, Double minPrice, Double maxPrice, 
                           String searchTerm, Integer pageNumber, Integer pageSize, 
                           ServiceCallback<List<Service>> callback) {
        Call<ApiResponse<ItemsWrapper<Service>>> call = serviceApiService.getServices(
            type, isActive, minPrice, maxPrice, searchTerm, pageNumber, pageSize
        );
        
        call.enqueue(new Callback<ApiResponse<ItemsWrapper<Service>>>() {
            @Override
            public void onResponse(Call<ApiResponse<ItemsWrapper<Service>>> call, Response<ApiResponse<ItemsWrapper<Service>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<ItemsWrapper<Service>> apiResponse = response.body();
                    List<Service> data = null;
                    if (apiResponse.getData() != null) {
                        data = apiResponse.getData().getItems();
                    }
                    if (apiResponse.isSucceeded() && data != null) {
                        callback.onSuccess(data);
                    } else {
                        callback.onError(apiResponse.getFirstErrorMessage());
                    }
                } else {
                    callback.onError("Failed to get services. Please try again.");
                }
            }
            
            @Override
            public void onFailure(Call<ApiResponse<ItemsWrapper<Service>>> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }
    
    /**
     * Get service by ID
     */
    public void getServiceById(int id, ServiceCallback<Service> callback) {
        Call<ApiResponse<Service>> call = serviceApiService.getServiceById(id);
        
        call.enqueue(new Callback<ApiResponse<Service>>() {
            @Override
            public void onResponse(Call<ApiResponse<Service>> call, Response<ApiResponse<Service>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Service> apiResponse = response.body();
                    if (apiResponse.isSucceeded()) {
                        callback.onSuccess(apiResponse.getData());
                    } else {
                        callback.onError(apiResponse.getFirstErrorMessage());
                    }
                } else {
                    callback.onError("Failed to get service. Please try again.");
                }
            }
            
            @Override
            public void onFailure(Call<ApiResponse<Service>> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }
    
    /**
     * Get services by type
     */
    public void getServicesByType(int type, ServiceCallback<List<Service>> callback) {
        Call<ApiResponse<ItemsWrapper<Service>>> call = serviceApiService.getServicesByType(type);

        call.enqueue(new Callback<ApiResponse<ItemsWrapper<Service>>>() {
            @Override
            public void onResponse(Call<ApiResponse<ItemsWrapper<Service>>> call, Response<ApiResponse<ItemsWrapper<Service>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<ItemsWrapper<Service>> apiResponse = response.body();
                    List<Service> data = null;
                    if (apiResponse.getData() != null) {
                        data = apiResponse.getData().getItems();
                    }
                    if (apiResponse.isSucceeded() && data != null) {
                        callback.onSuccess(data);
                    } else {
                        callback.onError(apiResponse.getFirstErrorMessage());
                    }
                } else {
                    callback.onError("Failed to get services by type. Please try again.");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<ItemsWrapper<Service>>> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }
    
    /**
     * Get popular services
     */
    public void getPopularServices(int limit, ServiceCallback<List<Service>> callback) {
        Call<ApiResponse<ItemsWrapper<Service>>> call = serviceApiService.getPopularServices(limit);

        call.enqueue(new Callback<ApiResponse<ItemsWrapper<Service>>>() {
            @Override
            public void onResponse(Call<ApiResponse<ItemsWrapper<Service>>> call, Response<ApiResponse<ItemsWrapper<Service>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<ItemsWrapper<Service>> apiResponse = response.body();
                    List<Service> data = null;
                    if (apiResponse.getData() != null) {
                        data = apiResponse.getData().getItems();
                    }
                    if (apiResponse.isSucceeded() && data != null) {
                        callback.onSuccess(data);
                    } else {
                        callback.onError(apiResponse.getFirstErrorMessage());
                    }
                } else {
                    callback.onError("Failed to get popular services. Please try again.");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<ItemsWrapper<Service>>> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }
} 