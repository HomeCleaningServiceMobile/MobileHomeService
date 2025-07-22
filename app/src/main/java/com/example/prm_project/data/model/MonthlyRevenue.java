package com.example.prm_project.data.model;

public class MonthlyRevenue {
    private String month;
    private long revenue;

    public MonthlyRevenue() {}

    public MonthlyRevenue(String month, long revenue) {
        this.month = month;
        this.revenue = revenue;
    }

    public String getMonth() { return month; }
    public void setMonth(String month) { this.month = month; }

    public long getRevenue() { return revenue; }
    public void setRevenue(long revenue) { this.revenue = revenue; }
}