package com.example.prm_project.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import com.google.gson.annotations.SerializedName;

@Entity(tableName = "users")
public class User {
    
    @PrimaryKey
    @SerializedName("id")
    private int id;
    
    @SerializedName("firstName")
    private String firstName;
    
    @SerializedName("lastName")
    private String lastName;
    
    @SerializedName("email")
    private String email;
    
    @SerializedName("phoneNumber")
    private String phoneNumber;
    
    @SerializedName("role")
    private String role;
    
    @SerializedName("status")
    private String status;
    
    @SerializedName("profileImageUrl")
    private String profileImageUrl;
    
    @SerializedName("address")
    private String address;
    
    @SerializedName("ward")
    private String ward;
    
    @SerializedName("district")
    private String district;
    
    @SerializedName("province")
    private String province;
    
    @SerializedName("country")
    private String country;
    
    @SerializedName("latitude")
    private Double latitude;
    
    @SerializedName("longitude")
    private Double longitude;
    
    @SerializedName("dateOfBirth")
    private String dateOfBirth;
    
    @SerializedName("gender")
    private String gender;
    
    @SerializedName("emergencyContactName")
    private String emergencyContactName;
    
    @SerializedName("emergencyContactPhone")
    private String emergencyContactPhone;
    
    @SerializedName("lastLoginAt")
    private String lastLoginAt;
    
    @SerializedName("emailVerifiedAt")
    private String emailVerifiedAt;
    
    @SerializedName("phoneVerifiedAt")
    private String phoneVerifiedAt;
    
    @SerializedName("createdAt")
    private String createdAt;
    
    // Constructors
    public User() {}
    
    public User(int id, String firstName, String lastName, String email, String phoneNumber) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }
    
    // Getters and Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getFirstName() {
        return firstName;
    }
    
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    public String getLastName() {
        return lastName;
    }
    
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setFullName(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) {
            this.firstName = "";
            this.lastName = "";
            return;
        }

        String[] parts = fullName.trim().split("\\s+");
        if (parts.length == 1) {
            this.firstName = parts[0];
            this.lastName = "";
        } else {
            this.firstName = parts[0];
            this.lastName = String.join(" ", java.util.Arrays.copyOfRange(parts, 1, parts.length));
        }
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPhoneNumber() {
        return phoneNumber;
    }
    
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    
    public String getRole() {
        return role;
    }
    
    public void setRole(String role) {
        this.role = role;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getProfileImageUrl() {
        return profileImageUrl;
    }
    
    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public String getWard() {
        return ward;
    }
    
    public void setWard(String ward) {
        this.ward = ward;
    }
    
    public String getDistrict() {
        return district;
    }
    
    public void setDistrict(String district) {
        this.district = district;
    }
    
    public String getProvince() {
        return province;
    }
    
    public void setProvince(String province) {
        this.province = province;
    }
    
    public String getCountry() {
        return country;
    }
    
    public void setCountry(String country) {
        this.country = country;
    }
    
    public Double getLatitude() {
        return latitude;
    }
    
    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }
    
    public Double getLongitude() {
        return longitude;
    }
    
    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
    
    public String getDateOfBirth() {
        return dateOfBirth;
    }
    
    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
    
    public String getGender() {
        return gender;
    }
    
    public void setGender(String gender) {
        this.gender = gender;
    }
    
    public String getEmergencyContactName() {
        return emergencyContactName;
    }
    
    public void setEmergencyContactName(String emergencyContactName) {
        this.emergencyContactName = emergencyContactName;
    }
    
    public String getEmergencyContactPhone() {
        return emergencyContactPhone;
    }
    
    public void setEmergencyContactPhone(String emergencyContactPhone) {
        this.emergencyContactPhone = emergencyContactPhone;
    }
    
    public String getLastLoginAt() {
        return lastLoginAt;
    }
    
    public void setLastLoginAt(String lastLoginAt) {
        this.lastLoginAt = lastLoginAt;
    }
    
    public String getEmailVerifiedAt() {
        return emailVerifiedAt;
    }
    
    public void setEmailVerifiedAt(String emailVerifiedAt) {
        this.emailVerifiedAt = emailVerifiedAt;
    }
    
    public String getPhoneVerifiedAt() {
        return phoneVerifiedAt;
    }
    
    public void setPhoneVerifiedAt(String phoneVerifiedAt) {
        this.phoneVerifiedAt = phoneVerifiedAt;
    }
    
    public String getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
    
    // Helper methods
    public String getFullName() {
        if (firstName != null && lastName != null) {
            return firstName + " " + lastName;
        } else if (firstName != null) {
            return firstName;
        } else if (lastName != null) {
            return lastName;
        }
        return "";
    }
    
    public String getFullAddress() {
        StringBuilder address = new StringBuilder();
        if (this.address != null) address.append(this.address);
        if (ward != null) address.append(", ").append(ward);
        if (district != null) address.append(", ").append(district);
        if (province != null) address.append(", ").append(province);
        if (country != null) address.append(", ").append(country);
        return address.toString();
    }
    
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", role='" + role + '\'' +
                ", status='" + status + '\'' +
                ", createdAt='" + createdAt + '\'' +
                '}';
    }
} 