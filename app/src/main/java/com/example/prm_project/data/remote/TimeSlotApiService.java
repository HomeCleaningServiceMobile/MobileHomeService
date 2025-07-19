package com.example.prm_project.data.remote;

import com.example.prm_project.data.model.ApiResponse;
import com.example.prm_project.data.model.StaffAvailabilityDto;
import com.example.prm_project.data.model.TimeSlotDto;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface TimeSlotApiService {
    
    /**
     * Get available time slots for a specific date
     * @param date Date in YYYY-MM-DD format
     * @param serviceId Optional service ID to filter by
     * @param staffId Optional staff ID to filter by
     * @return List of available time slots
     */
    @GET("api/timeslot/available-slots")
    Call<ApiResponse<List<TimeSlotDto>>> getAvailableSlots(
        @Query("date") String date,
        @Query("serviceId") Integer serviceId,
        @Query("staffId") Integer staffId
    );
    
    /**
     * Get available staff for a specific time slot
     * @param date Date in YYYY-MM-DD format
     * @param startTime Start time in HH:mm format
     * @param endTime End time in HH:mm format
     * @param serviceId Optional service ID to filter by
     * @return List of available staff
     */
    @GET("api/timeslot/available-staff-for-slot")
    Call<ApiResponse<List<StaffAvailabilityDto>>> getAvailableStaffForSlot(
        @Query("date") String date,
        @Query("startTime") String startTime,
        @Query("endTime") String endTime,
        @Query("serviceId") Integer serviceId
    );
    
    /**
     * Get available time slots for a date range
     * @param startDate Start date in YYYY-MM-DD format
     * @param endDate End date in YYYY-MM-DD format
     * @param serviceId Optional service ID to filter by
     * @param staffId Optional staff ID to filter by
     * @return Map of dates to available time slots
     */
    @GET("api/timeslot/available-slots-range")
    Call<ApiResponse<Map<String, List<TimeSlotDto>>>> getAvailableSlotsRange(
        @Query("startDate") String startDate,
        @Query("endDate") String endDate,
        @Query("serviceId") Integer serviceId,
        @Query("staffId") Integer staffId
    );
    
    /**
     * Get the next available time slot within the next 30 days
     * @param serviceId Optional service ID to filter by
     * @param staffId Optional staff ID to filter by
     * @return Next available time slot
     */
    @GET("api/timeslot/next-available-slot")
    Call<ApiResponse<TimeSlotDto>> getNextAvailableSlot(
        @Query("serviceId") Integer serviceId,
        @Query("staffId") Integer staffId
    );
} 