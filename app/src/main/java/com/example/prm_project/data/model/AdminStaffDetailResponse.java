package com.example.prm_project.data.model;

public class AdminStaffDetailResponse {
    private boolean isSucceeded;
    private String timestamp;
    private Object messages;
    private StaffDetail data;
    private Object pagination;

    public Object getPagination() {
        return pagination;
    }

    public StaffDetail getData() {
        return data;
    }

    public Object getMessages() {
        return messages;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public boolean isSucceeded() {
        return isSucceeded;
    }

    public static class StaffDetail {
        private int id;
        private String fullName;
        private String email;
        private String phoneNumber;
        private String bio;
        private String fullAddress;
        private String skills;
        private int hourlyRate;
        private String certificationImageUrl;
        private String idCardImageUrl;

        private boolean isAvailable;

        private String profileImageUrl;

        private String hireDate;

        public boolean isAvailable() {
            return isAvailable;
        }

        public String getProfileImageUrl() {
            return profileImageUrl;
        }

        public int getHourlyRate() {
            return hourlyRate;
        }

        public int getId() {
            return id;
        }

        public String getFullName() {
            return fullName;
        }

        public String getEmail() {
            return email;
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public String getBio() {
            return bio;
        }

        public String getFullAddress() {
            return fullAddress;
        }

        public String getSkills() {
            return skills;
        }

        public String getCertificationImageUrl() {
            return certificationImageUrl;
        }

        public String getIdCardImageUrl() {
            return idCardImageUrl;
        }

        public String getHireDate() {
            return hireDate;
        }
    }
}