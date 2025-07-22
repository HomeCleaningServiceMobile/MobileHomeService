package com.example.prm_project.data.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class TimeSlotDto {
    @SerializedName("startTime")
    private String startTime;
    
    @SerializedName("endTime")
    private String endTime;
    
    @SerializedName("displayTime")
    private String displayTime;
    
    @SerializedName("date")
    private String date;
    
    @SerializedName("isAvailable")
    private boolean isAvailable;
    
    @SerializedName("availableStaff")
    private List<StaffAvailabilityResponse> availableStaff;

    // Constructors
    public TimeSlotDto() {}

    public TimeSlotDto(String startTime, String endTime, String displayTime, String date, boolean isAvailable, List<StaffAvailabilityResponse> availableStaff) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.displayTime = displayTime;
        this.date = date;
        this.isAvailable = isAvailable;
        this.availableStaff = availableStaff;
    }

    // Getters and Setters
    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getDisplayTime() {
        return displayTime;
    }

    public void setDisplayTime(String displayTime) {
        this.displayTime = displayTime;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    public List<StaffAvailabilityResponse> getAvailableStaff() {
        return availableStaff;
    }

    public void setAvailableStaff(List<StaffAvailabilityResponse> availableStaff) {
        this.availableStaff = availableStaff;
    }
} 