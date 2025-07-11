package com.example.prm_project.data.model;

import com.google.gson.annotations.SerializedName;

public class ServicePackage {
    @SerializedName("id")
    private int id;
    
    @SerializedName("name")
    private String name;
    
    @SerializedName("description")
    private String description;
    
    @SerializedName("price")
    private double price;
    
    @SerializedName("durationMinutes")
    private int durationMinutes;
    
    @SerializedName("serviceId")
    private int serviceId;
    
    @SerializedName("isActive")
    private boolean isActive;

    // Default constructor
    public ServicePackage() {}

    // Constructor
    public ServicePackage(int id, String name, String description, double price, int durationMinutes, int serviceId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.durationMinutes = durationMinutes;
        this.serviceId = serviceId;
        this.isActive = true;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public int getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(int durationMinutes) { this.durationMinutes = durationMinutes; }

    public int getServiceId() { return serviceId; }
    public void setServiceId(int serviceId) { this.serviceId = serviceId; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    // Helper method to get duration in hours and minutes
    public String getDurationDisplayText() {
        int hours = durationMinutes / 60;
        int minutes = durationMinutes % 60;
        
        if (hours > 0 && minutes > 0) {
            return hours + "h " + minutes + "m";
        } else if (hours > 0) {
            return hours + "h";
        } else {
            return minutes + "m";
        }
    }
} 