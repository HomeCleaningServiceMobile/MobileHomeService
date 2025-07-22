package com.example.prm_project.data.model;

import java.util.List;
import java.util.Map;

/**
 * Response model cho việc tạo service mới trong admin
 */
public class AdminCreateServiceResponse {
    private boolean isSucceeded;
    private String timestamp;
    private Map<String, List<String>> messages;
    private ServiceData data;
    private Object pagination;

    public AdminCreateServiceResponse() {}

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

    public Map<String, List<String>> getMessages() {
        return messages;
    }

    public void setMessages(Map<String, List<String>> messages) {
        this.messages = messages;
    }

    public ServiceData getData() {
        return data;
    }

    public void setData(ServiceData data) {
        this.data = data;
    }

    public Object getPagination() {
        return pagination;
    }

    public void setPagination(Object pagination) {
        this.pagination = pagination;
    }

    /**
     * Inner class cho service data trong response
     */
    public static class ServiceData {
        private int id;
        private String name;
        private String description;
        private int type;
        private double basePrice;
        private double hourlyRate;
        private int estimatedDurationMinutes;
        private String imageUrl;
        private boolean isActive;
        private String requirements;
        private String restrictions;
        private String createdAt;
        private List<Object> servicePackages;

        public ServiceData() {}

        // Getters and Setters
        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public double getBasePrice() {
            return basePrice;
        }

        public void setBasePrice(double basePrice) {
            this.basePrice = basePrice;
        }

        public double getHourlyRate() {
            return hourlyRate;
        }

        public void setHourlyRate(double hourlyRate) {
            this.hourlyRate = hourlyRate;
        }

        public int getEstimatedDurationMinutes() {
            return estimatedDurationMinutes;
        }

        public void setEstimatedDurationMinutes(int estimatedDurationMinutes) {
            this.estimatedDurationMinutes = estimatedDurationMinutes;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        public boolean isActive() {
            return isActive;
        }

        public void setActive(boolean active) {
            isActive = active;
        }

        public String getRequirements() {
            return requirements;
        }

        public void setRequirements(String requirements) {
            this.requirements = requirements;
        }

        public String getRestrictions() {
            return restrictions;
        }

        public void setRestrictions(String restrictions) {
            this.restrictions = restrictions;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }

        public List<Object> getServicePackages() {
            return servicePackages;
        }

        public void setServicePackages(List<Object> servicePackages) {
            this.servicePackages = servicePackages;
        }
    }
}
