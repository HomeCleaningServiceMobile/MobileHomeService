package com.example.prm_project.data.remote;

import com.example.prm_project.data.model.ApiResponse;
import com.example.prm_project.data.model.Booking;
import com.example.prm_project.data.model.BookingStatus;
import com.example.prm_project.data.model.CreateBookingRequest;
import com.example.prm_project.data.model.Service;
import com.example.prm_project.data.model.ServicePackage;
import com.example.prm_project.data.model.ItemsWrapper;
import com.example.prm_project.data.model.Staff;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.*;

public interface BookingAPIService {

    @POST("{id}/respond")
    Call<ApiResponse> respondToBooking(
            @Path("id") int bookingId,
            @Body StaffResponseRequest request,
            @Header("Authorization") String token
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
    
    // Get staff bookings
    @GET("booking/staff/{staffId}")
    Call<ApiResponse<List<Booking>>> getStaffBookings(
        @Path("staffId") int staffId,
        @Query("status") Integer status,
        @Query("startDate") String startDate,
        @Query("endDate") String endDate,
        @Query("pageNumber") Integer pageNumber,
        @Query("pageSize") Integer pageSize
    );
    
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