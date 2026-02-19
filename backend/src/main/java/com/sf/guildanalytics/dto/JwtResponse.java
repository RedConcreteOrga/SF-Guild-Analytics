package com.sf.guildanalytics.dto;

public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private UserDTO user;

    public JwtResponse(String accessToken, UserDTO user) {
        this.token = accessToken;
        this.user = user;
    }

    // Getters and Setters
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }
}
