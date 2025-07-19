package com.example.prm_project.data.model;

import com.google.gson.annotations.SerializedName;

public class ServicePackage {
    @SerializedName("id")
    private int id;
    
    @SerializedName("serviceId")
    private int serviceId;
    
    @SerializedName("name")
    private String name;
    
    @SerializedName("description")
    private String description;
    
    @SerializedName("price")
    private double price;
    
    @SerializedName("durationMinutes")
    private int durationMinutes;
    
    @SerializedName("includedItems")
    private String includedItems;
    
    @SerializedName("isActive")
    private boolean isActive;
    
    @SerializedName("sortOrder")
    private int sortOrder;
    
    @SerializedName("createdAt")
    private String createdAt;

    // Default constructor
    public ServicePackage() {}

    // Constructor
    public ServicePackage(int id, int serviceId, String name, String description, double price, int durationMinutes) {
        this.id = id;
        this.serviceId = serviceId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.durationMinutes = durationMinutes;
        this.isActive = true;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getServiceId() { return serviceId; }
    public void setServiceId(int serviceId) { this.serviceId = serviceId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public int getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(int durationMinutes) { this.durationMinutes = durationMinutes; }

    public String getIncludedItems() { return includedItems; }
    public void setIncludedItems(String includedItems) { this.includedItems = includedItems; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public int getSortOrder() { return sortOrder; }
    public void setSortOrder(int sortOrder) { this.sortOrder = sortOrder; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
} 