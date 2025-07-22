package com.example.prm_project.data.model;

public class GoogleLoginResponse {
    private User user;
    private String accessToken;
    private String refreshToken;
    private String expiresAt;

    public User getUser() { return user; }
    public String getAccessToken() { return accessToken; }
    public String getRefreshToken() { return refreshToken; }
    public String getExpiresAt() { return expiresAt; }

    public AuthResponse toAuthResponse() {
        AuthResponse authResponse = new AuthResponse();
        authResponse.setAccessToken(accessToken);
        authResponse.setRefreshToken(refreshToken);
        authResponse.setExpiresAt(expiresAt);
        authResponse.setUser(user);
        return authResponse;
    }
}

