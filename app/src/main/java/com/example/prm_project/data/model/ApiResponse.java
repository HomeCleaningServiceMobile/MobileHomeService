package com.example.prm_project.data.model;

import com.google.gson.annotations.SerializedName;
import java.util.Map;

public class ApiResponse<T> {
    
    @SerializedName("isSucceeded")
    private boolean isSucceeded;
    
    @SerializedName("timestamp")
    private String timestamp;
    
    @SerializedName("messages")
    private Map<String, String[]> messages;
    
    @SerializedName("data")
    private T data;
    
    // Constructors
    public ApiResponse() {}
    
    public ApiResponse(boolean isSucceeded, String timestamp, Map<String, String[]> messages, T data) {
        this.isSucceeded = isSucceeded;
        this.timestamp = timestamp;
        this.messages = messages;
        this.data = data;
    }
    
    // Convenience constructor for simple success/error responses
    public ApiResponse(boolean isSucceeded, String message, T data) {
        this.isSucceeded = isSucceeded;
        this.timestamp = java.time.Instant.now().toString();
        this.data = data;
        
        // Create messages map with appropriate message
        this.messages = new java.util.HashMap<>();
        if (isSucceeded) {
            this.messages.put("Success", new String[]{message});
        } else {
            this.messages.put("Error", new String[]{message});
        }
    }
    
    // Getters and Setters
    public boolean isSucceeded() {
        return isSucceeded;
    }
    
    public void setSucceeded(boolean succeeded) {
        isSucceeded = succeeded;
    }
    
    public String getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
    
    public Map<String, String[]> getMessages() {
        return messages;
    }
    
    public void setMessages(Map<String, String[]> messages) {
        this.messages = messages;
    }
    
    public T getData() {
        return data;
    }
    
    public void setData(T data) {
        this.data = data;
    }
    
    // Helper methods
    public String getFirstErrorMessage() {
        if (messages != null && messages.containsKey("Error")) {
            String[] errors = messages.get("Error");
            if (errors != null && errors.length > 0) {
                return errors[0];
            }
        }
        return "Unknown error occurred";
    }
    
    public String getFirstSuccessMessage() {
        if (messages != null && messages.containsKey("Success")) {
            String[] successes = messages.get("Success");
            if (successes != null && successes.length > 0) {
                return successes[0];
            }
        }
        return "Operation completed successfully";
    }

    // Helper method for backend trả về data: { items: [...] }
    public <E> java.util.List<E> getItemsForList(Class<E> clazz) {
        if (data == null) return null;
        try {
            java.lang.reflect.Field itemsField = data.getClass().getDeclaredField("items");
            itemsField.setAccessible(true);
            Object items = itemsField.get(data);
            if (items instanceof java.util.List<?>) {
                return (java.util.List<E>) items;
            }
        } catch (Exception e) {
            // ignore
        }
        return null;
    }
} 