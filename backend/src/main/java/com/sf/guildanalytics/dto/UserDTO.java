package com.sf.guildanalytics.dto;

import java.util.UUID;

public class UserDTO {
    private UUID id;
    private String username;
    private String role;
    private UUID playerId;

    public UserDTO(UUID id, String username, String role, UUID playerId) {
        this.id = id;
        this.username = username;
        this.role = role;
        this.playerId = playerId;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public void setPlayerId(UUID playerId) {
        this.playerId = playerId;
    }
}
