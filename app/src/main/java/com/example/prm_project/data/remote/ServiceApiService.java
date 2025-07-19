package com.example.prm_project.data.remote;

import com.example.prm_project.data.model.ApiResponse;
import com.example.prm_project.data.model.Service;
import retrofit2.Call;
import retrofit2.http.*;
import com.example.prm_project.data.model.ItemsWrapper;
import java.util.List;
import com.example.prm_project.data.model.ServicePackage;

public interface ServiceApiService {
    
    /**
     * Get all services with filtering and pagination
     */
    @GET("services")
    Call<ApiResponse<ItemsWrapper<Service>>> getServices(
        @Query("type") Integer type,
        @Query("isActive") Boolean isActive,
        @Query("minPrice") Double minPrice,
        @Query("maxPrice") Double maxPrice,
        @Query("searchTerm") String searchTerm,
        @Query("pageNumber") Integer pageNumber,
        @Query("pageSize") Integer pageSize
    );
    
    /**
     * Get service by ID
     */
    @GET("services/{id}")
    Call<ApiResponse<Service>> getServiceById(@Path("id") int id);
    
    @GET("services/{id}/packages")
    Call<ApiResponse<List<ServicePackage>>> getServicePackages(@Path("id") int serviceId);
    
    /**
     * Get services by type
     */
    @GET("services/by-type/{type}")
    Call<ApiResponse<ItemsWrapper<Service>>> getServicesByType(@Path("type") int type);
    
    /**
     * Get popular services
     */
    @GET("services/popular")
    Call<ApiResponse<ItemsWrapper<Service>>> getPopularServices(@Query("limit") int limit);
} 