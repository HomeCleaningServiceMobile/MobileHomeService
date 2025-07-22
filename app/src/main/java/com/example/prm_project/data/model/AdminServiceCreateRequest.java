package com.example.prm_project.data.model;

public class AdminServiceCreateRequest {
    public String name;
    public String description;
    public int type;
    public double basePrice;
    public double hourlyRate;
    public int estimatedDurationMinutes;
    public String imageUrl;
    public String requirements;
    public String restrictions;

    public AdminServiceCreateRequest(String name, String description, int type, double basePrice, double hourlyRate, int estimatedDurationMinutes, String imageUrl, String requirements, String restrictions) {
        this.name = name;
        this.description = description;
        this.type = type;
        this.basePrice = basePrice;
        this.hourlyRate = hourlyRate;
        this.estimatedDurationMinutes = estimatedDurationMinutes;
        this.imageUrl = imageUrl;
        this.requirements = requirements;
        this.restrictions = restrictions;
    }
}

