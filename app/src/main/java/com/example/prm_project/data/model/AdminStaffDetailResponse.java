package com.example.prm_project.data.model;

public class AdminStaffDetailResponse {
    public boolean isSucceeded;
    public String timestamp;
    public Object messages;
    public StaffDetail data;
    public Object pagination;

    public static class StaffDetail {
        public int id;
        public String fullName;
        public String email;
        public String phoneNumber;
        public String bio;
        public String fullAddress;
        public String skills;
        public int hourlyRate;
        public String certificationImageUrl;
        public String idCardImageUrl;
    }
}