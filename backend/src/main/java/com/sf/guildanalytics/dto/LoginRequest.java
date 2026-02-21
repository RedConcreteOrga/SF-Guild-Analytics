package com.sf.guildanalytics.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class LoginRequest {

    @NotBlank(message = "Username must not be blank")
    @Size(max = 100, message = "Username too long")
    private String username;

    @NotBlank(message = "Password must not be blank")
    @Size(min = 8, max = 256, message = "Password length invalid")
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * SECURITY: Override toString() to prevent password from accidentally
     * appearing in logs, exceptions, or debug output.
     * Example risk: logger.debug("Login request: {}", loginRequest)
     */
    @Override
    public String toString() {
        return "LoginRequest{username='" + username + "', password='[PROTECTED]'}";
    }
}
