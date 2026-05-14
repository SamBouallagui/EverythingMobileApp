package com.example.everything.models.api;

public class CommunityDto {
    private int id;
    private String name;
    private String description;
    private String createdAt;
    private String createdByUsername;
    private boolean isActive;
    private int memberCount; 
    private boolean isJoined; 

    // Getters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getCreatedByUsername() { return createdByUsername; }
    public void setCreatedByUsername(String createdByUsername) { this.createdByUsername = createdByUsername; }

    public boolean getIsActive() { return isActive; }
    public void setIsActive(boolean active) { this.isActive = active; }

    // Champs de la réponse API
    public int getMemberCount() { return memberCount; }
    public void setMemberCount(int memberCount) { this.memberCount = memberCount; }

    public boolean isJoined() { return isJoined; }
    public void setJoined(boolean joined) { this.isJoined = joined; }
}
