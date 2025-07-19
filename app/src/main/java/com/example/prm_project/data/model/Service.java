package com.example.prm_project.data.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Service {
    @SerializedName("id")
    private int id;
    
    @SerializedName("name")
    private String name;
    
    @SerializedName("description")
    private String description;
    
    @SerializedName("type")
    private int type;
    
    @SerializedName("basePrice")
    private double basePrice;
    
    @SerializedName("hourlyRate")
    private Double hourlyRate;
    
    @SerializedName("estimatedDurationMinutes")
    private int estimatedDurationMinutes;
    
    @SerializedName("imageUrl")
    private String imageUrl;
    
    @SerializedName("isActive")
    private boolean isActive;
    
    @SerializedName("requirements")
    private String requirements;
    
    @SerializedName("restrictions")
    private String restrictions;
    
    @SerializedName("createdAt")
    private String createdAt;
    
    @SerializedName("servicePackages")
    private List<ServicePackage> servicePackages;

    // Default constructor
    public Service() {}

    // Constructor
    public Service(int id, String name, String description, double basePrice, int type) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.basePrice = basePrice;
        this.type = type;
        this.isActive = true;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getType() { return type; }
    public void setType(int type) { this.type = type; }

    public double getBasePrice() { return basePrice; }
    public void setBasePrice(double basePrice) { this.basePrice = basePrice; }

    public Double getHourlyRate() { return hourlyRate; }
    public void setHourlyRate(Double hourlyRate) { this.hourlyRate = hourlyRate; }

    public int getEstimatedDurationMinutes() { return estimatedDurationMinutes; }
    public void setEstimatedDurationMinutes(int estimatedDurationMinutes) { this.estimatedDurationMinutes = estimatedDurationMinutes; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public String getRequirements() { return requirements; }
    public void setRequirements(String requirements) { this.requirements = requirements; }

    public String getRestrictions() { return restrictions; }
    public void setRestrictions(String restrictions) { this.restrictions = restrictions; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public List<ServicePackage> getServicePackages() { return servicePackages; }
    public void setServicePackages(List<ServicePackage> servicePackages) { this.servicePackages = servicePackages; }
} 