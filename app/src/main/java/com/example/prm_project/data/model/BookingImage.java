package com.example.prm_project.data.model;

import com.google.gson.annotations.SerializedName;

public class BookingImage {
    @SerializedName("id")
    private int id;
    
    @SerializedName("imageUrl")
    private String imageUrl;
    
    @SerializedName("imageType")
    private String imageType;
    
    @SerializedName("description")
    private String description;
    
    @SerializedName("takenAt")
    private String takenAt;
    
    @SerializedName("takenBy")
    private String takenBy;

    // Default constructor
    public BookingImage() {}

    // Constructor
    public BookingImage(int id, String imageUrl, String imageType, String description, String takenAt, String takenBy) {
        this.id = id;
        this.imageUrl = imageUrl;
        this.imageType = imageType;
        this.description = description;
        this.takenAt = takenAt;
        this.takenBy = takenBy;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getImageType() { return imageType; }
    public void setImageType(String imageType) { this.imageType = imageType; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getTakenAt() { return takenAt; }
    public void setTakenAt(String takenAt) { this.takenAt = takenAt; }

    public String getTakenBy() { return takenBy; }
    public void setTakenBy(String takenBy) { this.takenBy = takenBy; }
} 