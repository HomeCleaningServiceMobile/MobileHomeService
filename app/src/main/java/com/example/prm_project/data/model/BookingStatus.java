package com.example.prm_project.data.model;

public enum BookingStatus {
    PENDING(0, "Pending"),
    CONFIRMED(1, "Confirmed"),
    AUTO_ASSIGNED(2, "Auto Assigned"),
    PENDING_SCHEDULE(3, "Pending Schedule"),
    IN_PROGRESS(4, "In Progress"),
    COMPLETED(5, "Completed"),
    CANCELLED(6, "Cancelled"),
    REJECTED(7, "Rejected");

    private final int value;
    private final String displayName;

    BookingStatus(int value, String displayName) {
        this.value = value;
        this.displayName = displayName;
    }

    public int getValue() {
        return value;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static BookingStatus fromValue(int value) {
        for (BookingStatus status : BookingStatus.values()) {
            if (status.value == value) {
                return status;
            }
        }
        return PENDING; // Default fallback
    }

    // Helper methods for UI logic
    public boolean canBeCancelled() {
        return this == PENDING || this == CONFIRMED || this == AUTO_ASSIGNED || this == PENDING_SCHEDULE;
    }

    public boolean canBeModified() {
        return this == PENDING || this == CONFIRMED;
    }

    public boolean isActive() {
        return this != COMPLETED && this != CANCELLED && this != REJECTED;
    }

    public boolean isCompleted() {
        return this == COMPLETED;
    }

    public boolean isCancelled() {
        return this == CANCELLED || this == REJECTED;
    }
} 