package com.sf.guildanalytics.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class GuildDTO {
    private UUID id;
    private String name;
    private String server;
    private String faction;
    private LocalDateTime createdAt;
    private int playerCount;
    private double avgLevel;
    private String imageUrl;

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getFaction() {
        return faction;
    }

    public void setFaction(String faction) {
        this.faction = faction;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public int getPlayerCount() {
        return playerCount;
    }

    public void setPlayerCount(int playerCount) {
        this.playerCount = playerCount;
    }

    public double getAvgLevel() {
        return avgLevel;
    }

    public void setAvgLevel(double avgLevel) {
        this.avgLevel = avgLevel;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
