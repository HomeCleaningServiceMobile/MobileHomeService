package com.example.prm_project.data.model;

public class UserSummary {
    private int totalCustomers;
    private int totalStaff;
    private int activeStaff;

    public UserSummary() {}

    public UserSummary(int totalCustomers, int totalStaff, int activeStaff) {
        this.totalCustomers = totalCustomers;
        this.totalStaff = totalStaff;
        this.activeStaff = activeStaff;
    }

    public int getTotalCustomers() { return totalCustomers; }
    public void setTotalCustomers(int totalCustomers) { this.totalCustomers = totalCustomers; }

    public int getTotalStaff() { return totalStaff; }
    public void setTotalStaff(int totalStaff) { this.totalStaff = totalStaff; }

    public int getActiveStaff() { return activeStaff; }
    public void setActiveStaff(int activeStaff) { this.activeStaff = activeStaff; }

    @Override
    public String toString() {
        return "UserSummary{" +
                "totalCustomers=" + totalCustomers +
                ", totalStaff=" + totalStaff +
                ", activeStaff=" + activeStaff +
                '}';
    }
}