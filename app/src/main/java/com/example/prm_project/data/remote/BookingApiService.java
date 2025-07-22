package com.example.prm_project.data.remote;

import com.example.prm_project.data.model.ApiResponse;
import com.example.prm_project.data.model.Booking;
import com.example.prm_project.data.model.BookingHistoryResponse;
import com.example.prm_project.data.model.BookingStatus;
import com.example.prm_project.data.model.CreateBookingRequest;
import com.example.prm_project.data.model.Service;
import com.example.prm_project.data.model.ServicePackage;
import com.example.prm_project.data.model.ItemsWrapper;
import com.example.prm_project.data.model.Staff;
import com.example.prm_project.data.model.TimeSlotDto;
import com.example.prm_project.data.model.StaffResponseRequest;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.*;

public interface BookingApiService {

    @POST("booking/{id}/respond")
    Call<ApiResponse> respondToBooking(
            @Path("id") int bookingId,
            @Body com.example.prm_project.data.model.StaffResponseRequest request
    );

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

    // Update booking status
    @PUT("booking/{id}/status")
    Call<ApiResponse<Booking>> updateBookingStatus(@Path("id") int bookingId, @Query("status") BookingStatus status);

    // Staff respond to booking
    @POST("booking/{id}/respond")
    Call<ApiResponse<Booking>> staffRespond(@Path("id") int bookingId, @Body StaffResponseRequest request);

    // Staff check-in
    @POST("booking/{id}/checkin")
    Call<ApiResponse<Booking>> staffCheckIn(@Path("id") int bookingId, @Body CheckInRequest request);

    // Staff check-out
    @POST("booking/{id}/checkout")
    Call<ApiResponse<Booking>> staffCheckOut(@Path("id") int bookingId, @Body CheckOutRequest request);

    // Auto-assign staff (Admin)
    @POST("booking/{id}/auto-assign")
    Call<ApiResponse<Booking>> autoAssignStaff(@Path("id") int bookingId);

    // Manual assign staff (Admin)
    @POST("booking/{id}/assign")
    Call<ApiResponse<Booking>> manualAssignStaff(@Path("id") int bookingId, @Body AssignStaffRequest request);

    // Force complete booking (Admin)
    @POST("booking/{id}/force-complete")
    Call<ApiResponse<Booking>> forceCompleteBooking(@Path("id") int bookingId, @Body ForceCompleteRequest request);

    // Customer confirm completion
    @POST("booking/{id}/confirm")
    Call<ApiResponse<Booking>> customerConfirmCompletion(@Path("id") int bookingId);

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

    // Get service packages for a service
    @GET("services/{serviceId}/packages")
    Call<ApiResponse<List<ServicePackage>>> getServicePackages(@Path("serviceId") int serviceId);

    // Get available time slots
    @GET("booking/available-slots")
    Call<ApiResponse<List<String>>> getAvailableTimeSlots(
        @Query("serviceId") int serviceId,
        @Query("date") String date,
        @Query("latitude") double latitude,
        @Query("longitude") double longitude
    );
    @GET("timeslot/service-slots")
    Call<ApiResponse<List<TimeSlotDto>>> getAvailableSlot(
            @Query("date") String date,
            @Query("serviceId") Integer serviceId
    );
    // Find available staff (Admin)
    @GET("booking/available-staff")
    Call<ApiResponse<List<Staff>>> getAvailableStaff(
        @Query("serviceId") int serviceId,
        @Query("scheduledDate") String scheduledDate,
        @Query("scheduledTime") String scheduledTime,
        @Query("latitude") double latitude,
        @Query("longitude") double longitude
    );
    
    // Get all bookings (Admin)
    @GET("booking/all")
    Call<ApiResponse<List<Booking>>> getAllBookings(
        @Query("status") Integer status,
        @Query("startDate") String startDate,
        @Query("endDate") String endDate,
        @Query("customerId") Integer customerId,
        @Query("staffId") Integer staffId,
        @Query("pageNumber") Integer pageNumber,
        @Query("pageSize") Integer pageSize
    );
    
    // Get staff bookings (backend will use UserId from token to get StaffId)
    @GET("booking/staff")
    Call<ApiResponse<List<Booking>>> getStaffBookings(
        @Query("status") Integer status,
        @Query("startDate") String startDate,
        @Query("endDate") String endDate,
        @Query("pageNumber") Integer pageNumber,
        @Query("pageSize") Integer pageSize
    );
    
    // Customer-specific booking endpoints
    
    // Get my bookings (enhanced with filtering and search)
    @GET("booking/my-bookings")
    Call<ApiResponse<List<Booking>>> getMyBookings(
        @Query("status") String status,
        @Query("startDate") String startDate,
        @Query("endDate") String endDate,
        @Query("serviceId") Integer serviceId,
        @Query("serviceName") String serviceName,
        @Query("searchTerm") String searchTerm,
        @Query("sortBy") String sortBy,
        @Query("sortDirection") String sortDirection,
        @Query("pageNumber") Integer pageNumber,
        @Query("pageSize") Integer pageSize
    );
    
    // Get my upcoming bookings
    @GET("booking/my-bookings/upcoming")
    Call<ApiResponse<List<Booking>>> getMyUpcomingBookings(
        @Query("days") Integer days
    );
    
    // Get my booking history with statistics
    @GET("booking/my-bookings/history")
    Call<ApiResponse<BookingHistoryResponse>> getMyBookingHistory();
    
    // Request classes for API endpoints
    class CancelBookingRequest {
        private String reason;
        
        public CancelBookingRequest(String reason) {
            this.reason = reason;
        }
        
        public String getReason() { return reason; }
        public void setReason(String reason) { this.reason = reason; }
    }
    
    class StaffResponseRequest {
        private boolean accept;
        private String declineReason;
        
        public StaffResponseRequest(boolean accept, String declineReason) {
            this.accept = accept;
            this.declineReason = declineReason;
        }
        
        public boolean isAccept() { return accept; }
        public void setAccept(boolean accept) { this.accept = accept; }
        public String getDeclineReason() { return declineReason; }
        public void setDeclineReason(String declineReason) { this.declineReason = declineReason; }
    }
    
    class CheckInRequest {
        private double currentLatitude;
        private double currentLongitude;
        private String notes;
        
        public CheckInRequest(double currentLatitude, double currentLongitude, String notes) {
            this.currentLatitude = currentLatitude;
            this.currentLongitude = currentLongitude;
            this.notes = notes;
        }
        
        public double getCurrentLatitude() { return currentLatitude; }
        public void setCurrentLatitude(double currentLatitude) { this.currentLatitude = currentLatitude; }
        public double getCurrentLongitude() { return currentLongitude; }
        public void setCurrentLongitude(double currentLongitude) { this.currentLongitude = currentLongitude; }
        public String getNotes() { return notes; }
        public void setNotes(String notes) { this.notes = notes; }
    }
    
    class CheckOutRequest {
        private String completionNotes;
        private List<String> completionImageUrls;
        
        public CheckOutRequest(String completionNotes, List<String> completionImageUrls) {
            this.completionNotes = completionNotes;
            this.completionImageUrls = completionImageUrls;
        }
        
        public String getCompletionNotes() { return completionNotes; }
        public void setCompletionNotes(String completionNotes) { this.completionNotes = completionNotes; }
        public List<String> getCompletionImageUrls() { return completionImageUrls; }
        public void setCompletionImageUrls(List<String> completionImageUrls) { this.completionImageUrls = completionImageUrls; }
    }
    
    class AssignStaffRequest {
        private int staffId;
        
        public AssignStaffRequest(int staffId) {
            this.staffId = staffId;
        }
        
        public int getStaffId() { return staffId; }
        public void setStaffId(int staffId) { this.staffId = staffId; }
    }
    
    class ForceCompleteRequest {
        private String reason;
        
        public ForceCompleteRequest(String reason) {
            this.reason = reason;
        }
        
        public String getReason() { return reason; }
        public void setReason(String reason) { this.reason = reason; }
    }
} 