package com.example.prm_project.data.model;

import com.google.gson.annotations.SerializedName;

public class StaffResponseRequest {

    @SerializedName("bookingId")
    private int bookingId;

    @SerializedName("accept")
    private boolean accept;

    @SerializedName("declineReason")
    private String declineReason;

    // Constructors
    public StaffResponseRequest() {}

    public StaffResponseRequest(int bookingId, boolean accept, String declineReason) {
        this.bookingId = bookingId;
        this.accept = accept;
        this.declineReason = declineReason;
    }

    // Getters and setters
    public int getBookingId() {
        return bookingId;
    }

    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

    public boolean isAccept() {
        return accept;
    }

    public void setAccept(boolean accept) {
        this.accept = accept;
    }

    public String getDeclineReason() {
        return declineReason;
    }

    public void setDeclineReason(String declineReason) {
        this.declineReason = declineReason;
    }
}
