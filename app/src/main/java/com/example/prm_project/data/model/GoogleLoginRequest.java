package com.example.prm_project.data.model;

public class GoogleLoginRequest {
    private String email;
    private String fullName;
    private String profileImageUrl;

    public GoogleLoginRequest(String email, String fullName, String profileImageUrl) {
        this.email = email;
        this.fullName = fullName;
        this.profileImageUrl = profileImageUrl;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }
}
