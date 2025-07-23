package com.example.prm_project.data.model;

import com.google.gson.annotations.SerializedName;

public class BookingStaff {
    @SerializedName("id")
    private int id;
    
    @SerializedName("name")
    private String name;
    
    @SerializedName("phoneNumber")
    private String phoneNumber;
    
    @SerializedName("rating")
    private double rating;

    // Default constructor
    public BookingStaff() {}

    // Constructor
    public BookingStaff(int id, String name, String phoneNumber, double rating) {
        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.rating = rating;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    // Helper method to get first name (split from full name)
    public String getFirstName() {
        if (name != null && name.contains(" ")) {
            return name.split(" ")[0];
        }
        return name != null ? name : "";
    }

    // Helper method to get last name (split from full name)
    public String getLastName() {
        if (name != null && name.contains(" ")) {
            String[] parts = name.split(" ");
            if (parts.length > 1) {
                return parts[parts.length - 1]; // Return the last part as last name
            }
        }
        return "";
    }

    // Helper method to get full name (for compatibility)
    public String getFullName() {
        return name != null ? name : "";
    }

    @Override
    public String toString() {
        return "BookingStaff{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", rating=" + rating +
                '}';
    }
} 