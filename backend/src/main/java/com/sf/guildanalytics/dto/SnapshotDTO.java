package com.sf.guildanalytics.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class SnapshotDTO {
    private UUID id;
    private UUID playerId;
    private LocalDateTime timestamp;
    private int level;
    private int strength;
    private int dexterity;
    private int intelligence;
    private int constitution;
    private long hp;
    private int honor;
    private double dungeonProgress;
    private int fortressLevel;

    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getPlayerId() { return playerId; }
    public void setPlayerId(UUID playerId) { this.playerId = playerId; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }
    public int getStrength() { return strength; }
    public void setStrength(int strength) { this.strength = strength; }
    public int getDexterity() { return dexterity; }
    public void setDexterity(int dexterity) { this.dexterity = dexterity; }
    public int getIntelligence() { return intelligence; }
    public void setIntelligence(int intelligence) { this.intelligence = intelligence; }
    public int getConstitution() { return constitution; }
    public void setConstitution(int constitution) { this.constitution = constitution; }
    public long getHp() { return hp; }
    public void setHp(long hp) { this.hp = hp; }
    public int getHonor() { return honor; }
    public void setHonor(int honor) { this.honor = honor; }
    public double getDungeonProgress() { return dungeonProgress; }
    public void setDungeonProgress(double dungeonProgress) { this.dungeonProgress = dungeonProgress; }
    public int getFortressLevel() { return fortressLevel; }
    public void setFortressLevel(int fortressLevel) { this.fortressLevel = fortressLevel; }
}
