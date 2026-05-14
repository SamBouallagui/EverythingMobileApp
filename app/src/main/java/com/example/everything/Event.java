package com.example.everything;
import com.example.everything.models.api.EventDto;
import java.io.Serializable;
public class Event implements Serializable {

    private String id;
    private String title;
    private String date;
    private String time;
    private String location;
    private String description;
    private int attendeeCount;
    private boolean isRsvped;
    private String communityId;
    private String authorId;
    private String authorName;

    public Event(String id, String title, String date, String time,
                 String location, String description,
                 int attendeeCount, boolean isRsvped, String communityId) {
        this.id = id;
        this.title = title;
        this.date = date;
        this.time = time;
        this.location = location;
        this.description = description;
        this.attendeeCount = attendeeCount;
        this.isRsvped = isRsvped;
        this.communityId = communityId;
        this.authorId = "0";
    }
    
    // Constructor
    public Event(EventDto dto) {
        this.id = String.valueOf(dto.getId());
        this.title = dto.getTitle();
        this.description = dto.getDescription();
        this.location = dto.getLocation();
        this.attendeeCount = dto.getAttendeeCount();
        this.isRsvped = dto.isParticipating();
        this.communityId = String.valueOf(dto.getCommunityId());
        this.authorId = String.valueOf(dto.getCreatedById());
        this.authorName = dto.getCreatedByUsername(); // Set author name
        
        this.date = formatDate(dto.getEventDate());
        this.time = formatTime(dto.getEventDate());
    }
    
    private String formatDate(String eventDate) {
        if (eventDate == null) return "Unknown date";
        try {
            java.text.SimpleDateFormat isoFormat = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", java.util.Locale.getDefault());
            java.util.Date date = isoFormat.parse(eventDate);
            if (date != null) {
                java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("MMMM d, yyyy", java.util.Locale.getDefault());
                return dateFormat.format(date);
            }
        } catch (Exception e) {

        }
        return "Unknown date";
    }
    
    private String formatTime(String eventDate) {
        if (eventDate == null) return "Unknown time";
        try {

            java.text.SimpleDateFormat isoFormat = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", java.util.Locale.getDefault());
            java.util.Date date = isoFormat.parse(eventDate);
            if (date != null) {
                java.text.SimpleDateFormat timeFormat = new java.text.SimpleDateFormat("h:mm a", java.util.Locale.getDefault());
                return timeFormat.format(date);
            }
        } catch (Exception e) {

        }
        return "Unknown time";
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getDate() { return date; }
    public String getTime() { return time; }
    public String getLocation() { return location; }
    public String getDescription() { return description; }
    public int getAttendeeCount() { return attendeeCount; }
    public boolean isRsvped() { return isRsvped; }
    public String getCommunityId() { return communityId; }
    public String getAuthorId() { return authorId; }
    public String getAuthorName() { return authorName; }
    public void setRsvped(boolean rsvped) { isRsvped = rsvped; }
    public void setAttendeeCount(int count) { attendeeCount = count; }
}
