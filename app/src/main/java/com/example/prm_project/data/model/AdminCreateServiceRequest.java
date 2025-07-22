package com.example.prm_project.data.model;

/**
 * Request model cho việc tạo service mới trong admin
 */
public class AdminCreateServiceRequest {
    private String name;
    private String description;
    private int type;
    private double basePrice;
    private double hourlyRate;
    private int estimatedDurationMinutes;
    private String imageUrl;
    private String requirements;
    private String restrictions;

    public AdminCreateServiceRequest() {}

    public AdminCreateServiceRequest(String name, String description, int type, 
                                   double basePrice, double hourlyRate, 
                                   int estimatedDurationMinutes, String imageUrl,
                                   String requirements, String restrictions) {
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

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public double getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(double basePrice) {
        this.basePrice = basePrice;
    }

    public double getHourlyRate() {
        return hourlyRate;
    }

    public void setHourlyRate(double hourlyRate) {
        this.hourlyRate = hourlyRate;
    }

    public int getEstimatedDurationMinutes() {
        return estimatedDurationMinutes;
    }

    public void setEstimatedDurationMinutes(int estimatedDurationMinutes) {
        this.estimatedDurationMinutes = estimatedDurationMinutes;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getRequirements() {
        return requirements;
    }

    public void setRequirements(String requirements) {
        this.requirements = requirements;
    }

    public String getRestrictions() {
        return restrictions;
    }

    public void setRestrictions(String restrictions) {
        this.restrictions = restrictions;
    }
}
