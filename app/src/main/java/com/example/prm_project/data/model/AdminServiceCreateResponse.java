package com.example.prm_project.data.model;

import java.util.Map;

public class AdminServiceCreateResponse {
    public boolean isSucceeded;
    public String timestamp;
    public Map<String, String[]> messages;
    public ServiceData data;
    public Object pagination;

    public static class ServiceData {
        public int id;
        public String name;
        public String description;
        public int type;
        public double basePrice;
        public double hourlyRate;
        public int estimatedDurationMinutes;
        public String imageUrl;
        public boolean isActive;
        public String requirements;
        public String restrictions;
        public String createdAt;
        public Object[] servicePackages;
    }
}

