package com.example.prm_project.data.model;

import com.google.gson.annotations.SerializedName;

public class Service {
    @SerializedName("id")
    private int id;
    
    @SerializedName("name")
    private String name;
    
    @SerializedName("description")
    private String description;
    
    @SerializedName("basePrice")
    private double basePrice;
    
    @SerializedName("type")
    private int type;
    
    @SerializedName("category")
    private String category;
    
    @SerializedName("imageUrl")
    private String imageUrl;
    
    @SerializedName("isActive")
    private boolean isActive;

    // Default constructor
    public Service() {}

    // Constructor
    public Service(int id, String name, String description, double basePrice, int type, String category) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.basePrice = basePrice;
        this.type = type;
        this.category = category;
        this.isActive = true;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getBasePrice() { return basePrice; }
    public void setBasePrice(double basePrice) { this.basePrice = basePrice; }

    public int getType() { return type; }
    public void setType(int type) { this.type = type; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
} 