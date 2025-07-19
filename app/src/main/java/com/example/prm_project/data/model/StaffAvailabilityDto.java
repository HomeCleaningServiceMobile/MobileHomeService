package com.example.prm_project.data.model;

import com.google.gson.annotations.SerializedName;

public class StaffAvailabilityDto {
    @SerializedName("staffId")
    private int staffId;
    
    @SerializedName("staffName")
    private String staffName;
    
    @SerializedName("position")
    private String position;

    // Constructors
    public StaffAvailabilityDto() {}

    public StaffAvailabilityDto(int staffId, String staffName, String position) {
        this.staffId = staffId;
        this.staffName = staffName;
        this.position = position;
    }

    // Getters and Setters
    public int getStaffId() {
        return staffId;
    }

    public void setStaffId(int staffId) {
        this.staffId = staffId;
    }

    public String getStaffName() {
        return staffName;
    }

    public void setStaffName(String staffName) {
        this.staffName = staffName;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }
} 