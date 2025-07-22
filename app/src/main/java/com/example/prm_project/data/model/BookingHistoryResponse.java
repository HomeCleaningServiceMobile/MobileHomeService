package com.example.prm_project.data.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class BookingHistoryResponse {
    
    @SerializedName("recentBookings")
    private List<Booking> recentBookings;
    
    @SerializedName("statistics")
    private BookingStatistics statistics;
    
    @SerializedName("topServices")
    private List<TopService> topServices;
    
    public static class BookingStatistics {
        @SerializedName("totalBookings")
        private int totalBookings;
        
        @SerializedName("completedBookings")
        private int completedBookings;
        
        @SerializedName("cancelledBookings")
        private int cancelledBookings;
        
        @SerializedName("pendingBookings")
        private int pendingBookings;
        
        @SerializedName("totalSpent")
        private double totalSpent;
        
        @SerializedName("averageRating")
        private double averageRating;
        
        @SerializedName("bookingsThisMonth")
        private int bookingsThisMonth;
        
        @SerializedName("bookingsThisYear")
        private int bookingsThisYear;
        
        // Getters and setters
        public int getTotalBookings() { return totalBookings; }
        public void setTotalBookings(int totalBookings) { this.totalBookings = totalBookings; }
        
        public int getCompletedBookings() { return completedBookings; }
        public void setCompletedBookings(int completedBookings) { this.completedBookings = completedBookings; }
        
        public int getCancelledBookings() { return cancelledBookings; }
        public void setCancelledBookings(int cancelledBookings) { this.cancelledBookings = cancelledBookings; }
        
        public int getPendingBookings() { return pendingBookings; }
        public void setPendingBookings(int pendingBookings) { this.pendingBookings = pendingBookings; }
        
        public double getTotalSpent() { return totalSpent; }
        public void setTotalSpent(double totalSpent) { this.totalSpent = totalSpent; }
        
        public double getAverageRating() { return averageRating; }
        public void setAverageRating(double averageRating) { this.averageRating = averageRating; }
        
        public int getBookingsThisMonth() { return bookingsThisMonth; }
        public void setBookingsThisMonth(int bookingsThisMonth) { this.bookingsThisMonth = bookingsThisMonth; }
        
        public int getBookingsThisYear() { return bookingsThisYear; }
        public void setBookingsThisYear(int bookingsThisYear) { this.bookingsThisYear = bookingsThisYear; }
    }
    
    public static class TopService {
        @SerializedName("serviceId")
        private int serviceId;
        
        @SerializedName("serviceName")
        private String serviceName;
        
        @SerializedName("bookingCount")
        private int bookingCount;
        
        @SerializedName("totalSpent")
        private double totalSpent;
        
        @SerializedName("lastUsed")
        private String lastUsed;
        
        // Getters and setters
        public int getServiceId() { return serviceId; }
        public void setServiceId(int serviceId) { this.serviceId = serviceId; }
        
        public String getServiceName() { return serviceName; }
        public void setServiceName(String serviceName) { this.serviceName = serviceName; }
        
        public int getBookingCount() { return bookingCount; }
        public void setBookingCount(int bookingCount) { this.bookingCount = bookingCount; }
        
        public double getTotalSpent() { return totalSpent; }
        public void setTotalSpent(double totalSpent) { this.totalSpent = totalSpent; }
        
        public String getLastUsed() { return lastUsed; }
        public void setLastUsed(String lastUsed) { this.lastUsed = lastUsed; }
    }
    
    // Main getters and setters
    public List<Booking> getRecentBookings() { return recentBookings; }
    public void setRecentBookings(List<Booking> recentBookings) { this.recentBookings = recentBookings; }
    
    public BookingStatistics getStatistics() { return statistics; }
    public void setStatistics(BookingStatistics statistics) { this.statistics = statistics; }
    
    public List<TopService> getTopServices() { return topServices; }
    public void setTopServices(List<TopService> topServices) { this.topServices = topServices; }
} 