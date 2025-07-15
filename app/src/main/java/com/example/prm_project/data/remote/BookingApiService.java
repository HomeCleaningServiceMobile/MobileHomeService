package com.example.prm_project.data.remote;

import com.example.prm_project.data.model.ApiResponse;
import com.example.prm_project.data.model.Booking;
import com.example.prm_project.data.model.CreateBookingRequest;
import com.example.prm_project.data.model.Service;
import com.example.prm_project.data.model.ServicePackage;
import com.example.prm_project.data.model.ItemsWrapper;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.*;

public interface BookingApiService {
    
    // Create a new booking
    @POST("booking")
    Call<ApiResponse<Booking>> createBooking(@Body CreateBookingRequest request);
    
    // Get booking by ID
    @GET("booking/{id}")
    Call<ApiResponse<Booking>> getBookingById(@Path("id") int bookingId);
    
    // Get bookings with filters
    @GET("booking")
    Call<ApiResponse<List<Booking>>> getBookings(
        @Query("status") Integer status,
        @Query("startDate") String startDate,
        @Query("endDate") String endDate,
        @Query("pageNumber") Integer pageNumber,
        @Query("pageSize") Integer pageSize
    );
    
    // Update booking
    @PUT("booking/{id}")
    Call<ApiResponse<Booking>> updateBooking(@Path("id") int bookingId, @Body CreateBookingRequest request);
    
    // Cancel booking
    @POST("booking/{id}/cancel")
    Call<ApiResponse<Booking>> cancelBooking(@Path("id") int bookingId, @Body CancelBookingRequest request);
    
    // Get available services
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
    
    // Get service packages for a service (mock endpoint)
    @GET("services/{serviceId}/packages")
    Call<ApiResponse<List<ServicePackage>>> getServicePackages(@Path("serviceId") int serviceId);
    
    // Get available time slots (mock endpoint)
    @GET("booking/available-slots")
    Call<ApiResponse<List<String>>> getAvailableTimeSlots(
        @Query("serviceId") int serviceId,
        @Query("date") String date,
        @Query("latitude") double latitude,
        @Query("longitude") double longitude
    );
    
    // Inner class for cancel booking request
    class CancelBookingRequest {
        private String reason;
        
        public CancelBookingRequest(String reason) {
            this.reason = reason;
        }
        
        public String getReason() { return reason; }
        public void setReason(String reason) { this.reason = reason; }
    }
} 