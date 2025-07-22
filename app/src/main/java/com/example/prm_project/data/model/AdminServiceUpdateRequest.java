package com.example.prm_project.data.model;

public class AdminServiceUpdateRequest {
    public String name;
    public String description;
    public double basePrice;
    public double hourlyRate;
    public int estimatedDurationMinutes;
    public String imageUrl;
    public String requirements;
    public String restrictions;
    public boolean isActive;

    public AdminServiceUpdateRequest(String name, String description, double basePrice, double hourlyRate, int estimatedDurationMinutes, String imageUrl, String requirements, String restrictions, boolean isActive) {
        this.name = name;
        this.description = description;
        this.basePrice = basePrice;
        this.hourlyRate = hourlyRate;
        this.estimatedDurationMinutes = estimatedDurationMinutes;
        this.imageUrl = imageUrl;
        this.requirements = requirements;
        this.restrictions = restrictions;
        this.isActive = isActive;
    }
}

