package com.example.prm_project.data.repository;

import com.example.prm_project.data.model.ApiResponse;
import com.example.prm_project.data.model.StaffAvailabilityDto;
import com.example.prm_project.data.model.TimeSlotDto;
import com.example.prm_project.data.remote.TimeSlotApiService;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.Call;

@Singleton
public class TimeSlotRepository {
    
    private final TimeSlotApiService timeSlotApiService;
    
    @Inject
    public TimeSlotRepository(TimeSlotApiService timeSlotApiService) {
        this.timeSlotApiService = timeSlotApiService;
    }
    
    /**
     * Get available time slots for a specific date
     */
    public Call<ApiResponse<List<TimeSlotDto>>> getAvailableSlots(String date, Integer serviceId, Integer staffId) {
        return timeSlotApiService.getAvailableSlots(date, serviceId, staffId);
    }
    
    /**
     * Get available staff for a specific time slot
     */
    public Call<ApiResponse<List<StaffAvailabilityDto>>> getAvailableStaffForSlot(
            String date, String startTime, String endTime, Integer serviceId) {
        return timeSlotApiService.getAvailableStaffForSlot(date, startTime, endTime, serviceId);
    }
    
    /**
     * Get available time slots for a date range
     */
    public Call<ApiResponse<Map<String, List<TimeSlotDto>>>> getAvailableSlotsRange(
            String startDate, String endDate, Integer serviceId, Integer staffId) {
        return timeSlotApiService.getAvailableSlotsRange(startDate, endDate, serviceId, staffId);
    }
    
    /**
     * Get the next available time slot within the next 30 days
     */
    public Call<ApiResponse<TimeSlotDto>> getNextAvailableSlot(Integer serviceId, Integer staffId) {
        return timeSlotApiService.getNextAvailableSlot(serviceId, staffId);
    }
} 