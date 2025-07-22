package com.example.prm_project.data.model;

import com.google.gson.annotations.SerializedName;

/**
 * Request model cho việc tạo staff mới
 */
public class AdminCreateStaffRequest {
    @SerializedName("firstName")
    private String firstName;

    @SerializedName("lastName")
    private String lastName;

    @SerializedName("email")
    private String email;

    @SerializedName("phoneNumber")
    private String phoneNumber;

    @SerializedName("password")
    private String password;

    @SerializedName("employeeId")
    private String employeeId;

    @SerializedName("hireDate")
    private String hireDate;

    @SerializedName("skills")
    private String skills;

    @SerializedName("bio")
    private String bio;

    @SerializedName("hourlyRate")
    private double hourlyRate;

    @SerializedName("serviceRadiusKm")
    private int serviceRadiusKm;

    @SerializedName("fullAddress")
    private String fullAddress;

    @SerializedName("street")
    private String street;

    @SerializedName("district")
    private String district;

    @SerializedName("city")
    private String city;

    @SerializedName("province")
    private String province;

    @SerializedName("postalCode")
    private String postalCode;

    @SerializedName("certificationImageUrl")
    private String certificationImageUrl;

    @SerializedName("idCardImageUrl")
    private String idCardImageUrl;

    // Constructors
    public AdminCreateStaffRequest() {}

    // Getters and Setters
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getHireDate() {
        return hireDate;
    }

    public void setHireDate(String hireDate) {
        this.hireDate = hireDate;
    }

    public String getSkills() {
        return skills;
    }

    public void setSkills(String skills) {
        this.skills = skills;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public double getHourlyRate() {
        return hourlyRate;
    }

    public void setHourlyRate(double hourlyRate) {
        this.hourlyRate = hourlyRate;
    }

    public double getServiceRadiusKm() {
        return serviceRadiusKm;
    }

    public void setServiceRadiusKm(int serviceRadiusKm) {
        this.serviceRadiusKm = serviceRadiusKm;
    }

    public String getFullAddress() {
        return fullAddress;
    }

    public void setFullAddress(String fullAddress) {
        this.fullAddress = fullAddress;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCertificationImageUrl() {
        return certificationImageUrl;
    }

    public void setCertificationImageUrl(String certificationImageUrl) {
        this.certificationImageUrl = certificationImageUrl;
    }

    public String getIdCardImageUrl() {
        return idCardImageUrl;
    }

    public void setIdCardImageUrl(String idCardImageUrl) {
        this.idCardImageUrl = idCardImageUrl;
    }

    @Override
    public String toString() {
        return "AdminCreateStaffRequest{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", employeeId='" + employeeId + '\'' +
                ", hireDate='" + hireDate + '\'' +
                ", skills='" + skills + '\'' +
                ", bio='" + bio + '\'' +
                ", hourlyRate=" + hourlyRate +
                ", serviceRadiusKm=" + serviceRadiusKm +
                ", fullAddress='" + fullAddress + '\'' +
                ", street='" + street + '\'' +
                ", district='" + district + '\'' +
                ", city='" + city + '\'' +
                ", province='" + province + '\'' +
                ", postalCode='" + postalCode + '\'' +
                ", certificationImageUrl='" + certificationImageUrl + '\'' +
                ", idCardImageUrl='" + idCardImageUrl + '\'' +
                '}';
    }
}
