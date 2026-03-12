package com.example.everything;
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
    public void setRsvped(boolean rsvped) { isRsvped = rsvped; }
    public void setAttendeeCount(int count) { attendeeCount = count; }
}
