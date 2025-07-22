package com.example.prm_project.data.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * Response model cho danh sách services từ admin API
 */
public class AdminServiceListResponse {
    @SerializedName("isSucceeded")
    private boolean isSucceeded;

    @SerializedName("timestamp")
    private String timestamp;

    @SerializedName("messages")
    private Messages messages;

    @SerializedName("data")
    private ServiceData data;

    @SerializedName("pagination")
    private Object pagination;

    // Constructors
    public AdminServiceListResponse() {}

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

    public Messages getMessages() {
        return messages;
    }

    public void setMessages(Messages messages) {
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

    // Inner Classes
    public static class Messages {
        @SerializedName("Success")
        private List<String> success;

        public List<String> getSuccess() {
            return success;
        }

        public void setSuccess(List<String> success) {
            this.success = success;
        }
    }

    public static class ServiceData {
        @SerializedName("items")
        private List<ServiceItem> items;

        @SerializedName("pageNumber")
        private int pageNumber;

        @SerializedName("pageSize")
        private int pageSize;

        @SerializedName("totalCount")
        private int totalCount;

        @SerializedName("totalPages")
        private int totalPages;

        @SerializedName("hasPreviousPage")
        private boolean hasPreviousPage;

        @SerializedName("hasNextPage")
        private boolean hasNextPage;

        // Getters and Setters
        public List<ServiceItem> getItems() {
            return items;
        }

        public void setItems(List<ServiceItem> items) {
            this.items = items;
        }

        public int getPageNumber() {
            return pageNumber;
        }

        public void setPageNumber(int pageNumber) {
            this.pageNumber = pageNumber;
        }

        public int getPageSize() {
            return pageSize;
        }

        public void setPageSize(int pageSize) {
            this.pageSize = pageSize;
        }

        public int getTotalCount() {
            return totalCount;
        }

        public void setTotalCount(int totalCount) {
            this.totalCount = totalCount;
        }

        public int getTotalPages() {
            return totalPages;
        }

        public void setTotalPages(int totalPages) {
            this.totalPages = totalPages;
        }

        public boolean isHasPreviousPage() {
            return hasPreviousPage;
        }

        public void setHasPreviousPage(boolean hasPreviousPage) {
            this.hasPreviousPage = hasPreviousPage;
        }

        public boolean isHasNextPage() {
            return hasNextPage;
        }

        public void setHasNextPage(boolean hasNextPage) {
            this.hasNextPage = hasNextPage;
        }
    }

    public static class ServiceItem {
        @SerializedName("id")
        private int id;

        @SerializedName("name")
        private String name;

        @SerializedName("description")
        private String description;

        @SerializedName("type")
        private int type;

        @SerializedName("basePrice")
        private double basePrice;

        @SerializedName("imageUrl")
        private String imageUrl;

        @SerializedName("isActive")
        private boolean isActive;

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
    }
}
