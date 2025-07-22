package com.example.prm_project.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.Map;

public class AdminUpdateStaffResponse {
    @SerializedName("isSucceeded")
    private boolean isSucceeded;

    @SerializedName("messages")
    private Map<String, String> messages;

    // Getter và Setter
    public boolean isSucceeded() {
        return isSucceeded;
    }

    public void setSucceeded(boolean succeeded) {
        isSucceeded = succeeded;
    }

    public Map<String, String> getMessages() {
        return messages;
    }

    public void setMessages(Map<String, String> messages) {
        this.messages = messages;
    }

}
