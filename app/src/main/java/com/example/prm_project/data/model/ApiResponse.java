package com.example.prm_project.data.model;

public class ApiResponse {
    private boolean isSucceeded;
    private String message;
    private Object data;

    // Constructors
    public ApiResponse() {}

    public ApiResponse(boolean isSucceeded, String message, Object data) {
        this.isSucceeded = isSucceeded;
        this.message = message;
        this.data = data;
    }

    // Getters and setters
    public boolean isSucceeded() { return isSucceeded; }
    public void setSucceeded(boolean succeeded) { isSucceeded = succeeded; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Object getData() { return data; }
    public void setData(Object data) { this.data = data; }
}
