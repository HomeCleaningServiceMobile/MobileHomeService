package com.example.prm_project.data.model;

public class TopService {
    private String serviceName;
    private long totalRevenue;
    private int bookingCount;

    public TopService() {}

    public TopService(String serviceName, long totalRevenue, int bookingCount) {
        this.serviceName = serviceName;
        this.totalRevenue = totalRevenue;
        this.bookingCount = bookingCount;
    }

    public String getServiceName() { return serviceName; }
    public void setServiceName(String serviceName) { this.serviceName = serviceName; }

    public long getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(long totalRevenue) { this.totalRevenue = totalRevenue; }

    public int getBookingCount() { return bookingCount; }
    public void setBookingCount(int bookingCount) { this.bookingCount = bookingCount; }
}