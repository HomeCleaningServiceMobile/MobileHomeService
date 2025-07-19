package com.example.prm_project.data.model;

import com.google.gson.annotations.SerializedName;

public class Review {
    @SerializedName("id")
    private int id;
    
    @SerializedName("rating")
    private int rating;
    
    @SerializedName("comment")
    private String comment;
    
    @SerializedName("createdAt")
    private String createdAt;

    // Default constructor
    public Review() {}

    // Constructor
    public Review(int id, int rating, String comment, String createdAt) {
        this.id = id;
        this.rating = rating;
        this.comment = comment;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
} 