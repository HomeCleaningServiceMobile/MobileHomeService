package com.example.prm_project.data.remote;

import com.example.prm_project.data.model.ApiResponse;
import com.example.prm_project.data.model.Staff;
import com.example.prm_project.data.model.StaffAvailabilityResponse;
import com.example.prm_project.data.model.GetStaffProfileResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface StaffApiService {
    
    /**
     * Get all staff profiles with pagination
     * @param pageNumber Page number (default: 1)
     * @param pageSize Items per page (default: 10)
     * @return Paginated list of staff profiles
     */
    @GET("staff")
    Call<ApiResponse<List<Staff>>> getAllStaffProfiles(
        @Query("pageNumber") Integer pageNumber,
        @Query("pageSize") Integer pageSize
    );
    
    /**
     * Get staff profile by employee ID
     * @param employeeId The employee ID of the staff member
     * @return Staff profile details
     */
    @GET("staff/{employeeId}")
    Call<ApiResponse<GetStaffProfileResponse>> getStaffProfileByEmployeeId(
        @Path("employeeId") String employeeId
    );
    
    /**
     * Get available staff for a specific service
     * @param date Date in YYYY-MM-DD format
     * @param startTime Start time in HH:mm format
     * @param endTime End time in HH:mm format
     * @param serviceId Service ID to filter by
     * @return List of available staff for the service
     */
    @GET("timeslots/service-staff")
    Call<ApiResponse<List<StaffAvailabilityResponse>>> getAvailableStaffForService(
        @Query("date") String date,
        @Query("startTime") String startTime,
        @Query("endTime") String endTime,
        @Query("serviceId") Integer serviceId
    );
    
    /**
     * Find available staff for service location
     * @param serviceId Service ID
     * @param scheduledDate Scheduled date (YYYY-MM-DD)
     * @param scheduledTime Scheduled time (HH:mm:ss)
     * @param latitude Service location latitude
     * @param longitude Service location longitude
     * @return List of available staff with distance information
     */
    @GET("booking/available-staff")
    Call<ApiResponse<List<StaffAvailabilityResponse>>> getAvailableStaffForLocation(
        @Query("serviceId") Integer serviceId,
        @Query("scheduledDate") String scheduledDate,
        @Query("scheduledTime") String scheduledTime,
        @Query("latitude") Double latitude,
        @Query("longitude") Double longitude
    );
} 