package com.example.prm_project.data.model;

import com.google.gson.annotations.SerializedName;
import java.util.Date;
import java.util.List;

public class Booking {
    @SerializedName("id")
    private int id;
    
    @SerializedName("bookingNumber")
    private String bookingNumber;
    
    @SerializedName("status")
    private int status;
    
    @SerializedName("scheduledDate")
    private String scheduledDate;
    
    @SerializedName("scheduledTime")
    private String scheduledTime;
    
    @SerializedName("estimatedDurationMinutes")
    private int estimatedDurationMinutes;
    
    @SerializedName("totalAmount")
    private double totalAmount;
    
    @SerializedName("finalAmount")
    private Double finalAmount;
    
    @SerializedName("serviceAddress")
    private String serviceAddress;
    
    @SerializedName("addressLatitude")
    private Double addressLatitude;
    
    @SerializedName("addressLongitude")
    private Double addressLongitude;
    
    @SerializedName("specialInstructions")
    private String specialInstructions;
    
    @SerializedName("notes")
    private String notes;
    
    @SerializedName("startedAt")
    private String startedAt;
    
    @SerializedName("completedAt")
    private String completedAt;
    
    @SerializedName("cancelledAt")
    private String cancelledAt;
    
    @SerializedName("cancellationReason")
    private String cancellationReason;
    
    @SerializedName("createdAt")
    private String createdAt;
    
    @SerializedName("preferredPaymentMethod")
    private int preferredPaymentMethod;
    
    @SerializedName("customer")
    private Customer customer;
    
    @SerializedName("service")
    private Service service;
    
    @SerializedName("servicePackage")
    private ServicePackage servicePackage;
    
    @SerializedName("staff")
    private Staff staff;
    
    @SerializedName("payment")
    private Payment payment;
    
    @SerializedName("review")
    private Review review;
    
    @SerializedName("bookingImages")
    private List<BookingImage> bookingImages;

    // Default constructor
    public Booking() {}

    // Constructor for creating new booking
    public Booking(int serviceId, int servicePackageId, String scheduledDate, String scheduledTime, 
                   String serviceAddress, double addressLatitude, double addressLongitude, 
                   String specialInstructions, int preferredPaymentMethod) {
        this.service = new Service();
        this.service.setId(serviceId);
        this.servicePackage = new ServicePackage();
        this.servicePackage.setId(servicePackageId);
        this.scheduledDate = scheduledDate;
        this.scheduledTime = scheduledTime;
        this.serviceAddress = serviceAddress;
        this.addressLatitude = addressLatitude;
        this.addressLongitude = addressLongitude;
        this.specialInstructions = specialInstructions;
        this.preferredPaymentMethod = preferredPaymentMethod;
        this.status = BookingStatus.PENDING.getValue();
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getBookingNumber() { return bookingNumber; }
    public void setBookingNumber(String bookingNumber) { this.bookingNumber = bookingNumber; }

    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }

    public String getScheduledDate() { return scheduledDate; }
    public void setScheduledDate(String scheduledDate) { this.scheduledDate = scheduledDate; }

    public String getScheduledTime() { return scheduledTime; }
    public void setScheduledTime(String scheduledTime) { this.scheduledTime = scheduledTime; }

    public int getEstimatedDurationMinutes() { return estimatedDurationMinutes; }
    public void setEstimatedDurationMinutes(int estimatedDurationMinutes) { this.estimatedDurationMinutes = estimatedDurationMinutes; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public Double getFinalAmount() { return finalAmount; }
    public void setFinalAmount(Double finalAmount) { this.finalAmount = finalAmount; }

    public String getServiceAddress() { return serviceAddress; }
    public void setServiceAddress(String serviceAddress) { this.serviceAddress = serviceAddress; }

    public Double getAddressLatitude() { return addressLatitude; }
    public void setAddressLatitude(Double addressLatitude) { this.addressLatitude = addressLatitude; }

    public Double getAddressLongitude() { return addressLongitude; }
    public void setAddressLongitude(Double addressLongitude) { this.addressLongitude = addressLongitude; }

    public String getSpecialInstructions() { return specialInstructions; }
    public void setSpecialInstructions(String specialInstructions) { this.specialInstructions = specialInstructions; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getStartedAt() { return startedAt; }
    public void setStartedAt(String startedAt) { this.startedAt = startedAt; }

    public String getCompletedAt() { return completedAt; }
    public void setCompletedAt(String completedAt) { this.completedAt = completedAt; }

    public String getCancelledAt() { return cancelledAt; }
    public void setCancelledAt(String cancelledAt) { this.cancelledAt = cancelledAt; }

    public String getCancellationReason() { return cancellationReason; }
    public void setCancellationReason(String cancellationReason) { this.cancellationReason = cancellationReason; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public int getPreferredPaymentMethod() { return preferredPaymentMethod; }
    public void setPreferredPaymentMethod(int preferredPaymentMethod) { this.preferredPaymentMethod = preferredPaymentMethod; }

    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }

    public Service getService() { return service; }
    public void setService(Service service) { this.service = service; }

    public ServicePackage getServicePackage() { return servicePackage; }
    public void setServicePackage(ServicePackage servicePackage) { this.servicePackage = servicePackage; }

    public Staff getStaff() { return staff; }
    public void setStaff(Staff staff) { this.staff = staff; }

    public Payment getPayment() { return payment; }
    public void setPayment(Payment payment) { this.payment = payment; }

    public Review getReview() { return review; }
    public void setReview(Review review) { this.review = review; }

    public List<BookingImage> getBookingImages() { return bookingImages; }
    public void setBookingImages(List<BookingImage> bookingImages) { this.bookingImages = bookingImages; }

    // Helper method to get status as enum
    public BookingStatus getStatusEnum() {
        return BookingStatus.fromValue(status);
    }

    // Helper method to get payment method as enum
    public PaymentMethod getPaymentMethodEnum() {
        return PaymentMethod.fromValue(preferredPaymentMethod);
    }

    // Helper method to get status display text
    public String getStatusDisplayText() {
        return BookingStatus.fromValue(status).getDisplayName();
    }

    // Helper method to get payment method display text
    public String getPaymentMethodDisplayText() {
        return PaymentMethod.fromValue(preferredPaymentMethod).getDisplayName();
    }
} 