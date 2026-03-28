package com.example.everything.models.api;

public class EventDto {
    private int id;
    private int communityId;
    private int createdById;
    private String title;
    private String description;
    private String eventDate;
    private String location;
    private String createdAt;
    private boolean isParticipating;
    private int participantCount;
    private String createdByUsername;

    // Getters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getCommunityId() { return communityId; }
    public void setCommunityId(int communityId) { this.communityId = communityId; }

    public int getCreatedById() { return createdById; }
    public void setCreatedById(int createdById) { this.createdById = createdById; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getEventDate() { return eventDate; }
    public void setEventDate(String eventDate) { this.eventDate = eventDate; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public boolean isParticipating() { return isParticipating; }
    public void setParticipating(boolean participating) { isParticipating = participating; }

    public int getParticipantCount() { return participantCount; }
    public void setParticipantCount(int participantCount) { this.participantCount = participantCount; }
    
    public String getCreatedByUsername() { return createdByUsername; }
    public void setCreatedByUsername(String createdByUsername) { this.createdByUsername = createdByUsername; }


    public int getAttendeeCount() { return participantCount; }
    public void setAttendeeCount(int attendeeCount) { this.participantCount = attendeeCount; }
}
