package com.example.prm_project.data.model;

import com.google.gson.annotations.SerializedName;

public class CreateBookingRequest {
    @SerializedName("serviceId")
    private int serviceId;
    
    @SerializedName("servicePackageId")
    private int servicePackageId;
    
    @SerializedName("scheduledDate")
    private String scheduledDate;
    
    @SerializedName("scheduledTime")
    private String scheduledTime;
    
    @SerializedName("serviceAddress")
    private String serviceAddress;
    
    @SerializedName("addressLatitude")
    private double addressLatitude;
    
    @SerializedName("addressLongitude")
    private double addressLongitude;
    
    @SerializedName("specialInstructions")
    private String specialInstructions;
    
    @SerializedName("preferredPaymentMethod")
    private int preferredPaymentMethod;

    // Default constructor
    public CreateBookingRequest() {}

    // Constructor
    public CreateBookingRequest(int serviceId, int servicePackageId, String scheduledDate, String scheduledTime,
                               String serviceAddress, double addressLatitude, double addressLongitude,
                               String specialInstructions, int preferredPaymentMethod) {
        this.serviceId = serviceId;
        this.servicePackageId = servicePackageId;
        this.scheduledDate = scheduledDate;
        this.scheduledTime = scheduledTime;
        this.serviceAddress = serviceAddress;
        this.addressLatitude = addressLatitude;
        this.addressLongitude = addressLongitude;
        this.specialInstructions = specialInstructions;
        this.preferredPaymentMethod = preferredPaymentMethod;
    }

    // Getters and Setters
    public int getServiceId() { return serviceId; }
    public void setServiceId(int serviceId) { this.serviceId = serviceId; }

    public int getServicePackageId() { return servicePackageId; }
    public void setServicePackageId(int servicePackageId) { this.servicePackageId = servicePackageId; }

    public String getScheduledDate() { return scheduledDate; }
    public void setScheduledDate(String scheduledDate) { this.scheduledDate = scheduledDate; }

    public String getScheduledTime() { return scheduledTime; }
    public void setScheduledTime(String scheduledTime) { this.scheduledTime = scheduledTime; }

    public String getServiceAddress() { return serviceAddress; }
    public void setServiceAddress(String serviceAddress) { this.serviceAddress = serviceAddress; }

    public double getAddressLatitude() { return addressLatitude; }
    public void setAddressLatitude(double addressLatitude) { this.addressLatitude = addressLatitude; }

    public double getAddressLongitude() { return addressLongitude; }
    public void setAddressLongitude(double addressLongitude) { this.addressLongitude = addressLongitude; }

    public String getSpecialInstructions() { return specialInstructions; }
    public void setSpecialInstructions(String specialInstructions) { this.specialInstructions = specialInstructions; }

    public int getPreferredPaymentMethod() { return preferredPaymentMethod; }
    public void setPreferredPaymentMethod(int preferredPaymentMethod) { this.preferredPaymentMethod = preferredPaymentMethod; }
} 