package com.example.prm_project.data.model;

import com.google.gson.annotations.SerializedName;

public class Staff {
    @SerializedName("id")
    private int id;
    
    @SerializedName("firstName")
    private String firstName;
    
    @SerializedName("lastName")
    private String lastName;
    
    @SerializedName("email")
    private String email;
    
    @SerializedName("phoneNumber")
    private String phoneNumber;
    
    @SerializedName("hourlyRate")
    private double hourlyRate;
    
    @SerializedName("rating")
    private double rating;

    // Default constructor
    public Staff() {}

    // Constructor
    public Staff(int id, String firstName, String lastName, String email, String phoneNumber, double hourlyRate, double rating) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.hourlyRate = hourlyRate;
        this.rating = rating;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public double getHourlyRate() { return hourlyRate; }
    public void setHourlyRate(double hourlyRate) { this.hourlyRate = hourlyRate; }

    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }

    // Helper method to get full name
    public String getFullName() {
        return firstName + " " + lastName;
    }
} 