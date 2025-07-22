package com.example.prm_project.data.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class StaffAvailabilityResponse implements Serializable {
    
    @SerializedName("staffId")
    private Integer staffId;
    
    @SerializedName("staffName")
    private String staffName;
    
    @SerializedName("employeeId")
    private String employeeId;
    
    @SerializedName("hourlyRate")
    private Double hourlyRate;
    
    @SerializedName("averageRating")
    private Double averageRating;
    
    @SerializedName("totalCompletedJobs")
    private Integer totalCompletedJobs;
    
    @SerializedName("isAvailable")
    private boolean isAvailable;
    
    @SerializedName("serviceId")
    private Integer serviceId;
    
    @SerializedName("serviceName")
    private String serviceName;
    
    @SerializedName("distanceKm")
    private Double distanceKm;

    // Constructors
    public StaffAvailabilityResponse() {}

    public StaffAvailabilityResponse(Integer staffId, String staffName, String employeeId) {
        this.staffId = staffId;
        this.staffName = staffName;
        this.employeeId = employeeId;
    }

    // Getters and Setters
    public Integer getStaffId() {
        return staffId;
    }

    public void setStaffId(Integer staffId) {
        this.staffId = staffId;
    }

    public String getStaffName() {
        return staffName;
    }

    public void setStaffName(String staffName) {
        this.staffName = staffName;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public Double getHourlyRate() {
        return hourlyRate;
    }

    public void setHourlyRate(Double hourlyRate) {
        this.hourlyRate = hourlyRate;
    }

    public Double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
    }

    public Integer getTotalCompletedJobs() {
        return totalCompletedJobs;
    }

    public void setTotalCompletedJobs(Integer totalCompletedJobs) {
        this.totalCompletedJobs = totalCompletedJobs;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    public Integer getServiceId() {
        return serviceId;
    }

    public void setServiceId(Integer serviceId) {
        this.serviceId = serviceId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public Double getDistanceKm() {
        return distanceKm;
    }

    public void setDistanceKm(Double distanceKm) {
        this.distanceKm = distanceKm;
    }

    @Override
    public String toString() {
        return "StaffAvailabilityResponse{" +
                "staffId=" + staffId +
                ", staffName='" + staffName + '\'' +
                ", employeeId='" + employeeId + '\'' +
                ", hourlyRate=" + hourlyRate +
                ", averageRating=" + averageRating +
                ", totalCompletedJobs=" + totalCompletedJobs +
                ", isAvailable=" + isAvailable +
                ", serviceId=" + serviceId +
                ", serviceName='" + serviceName + '\'' +
                ", distanceKm=" + distanceKm +
                '}';
    }
} 