package com.example.everything;

import java.io.Serializable;

public class Member implements Serializable {

    private String id;
    private String name;
    private String role; // "member", "moderator", "admin"
    private String joinDate;
    private String avatarUrl;

    public Member(String id, String name, String role,
                  String joinDate, String avatarUrl) {
        this.id = id;
        this.name = name;
        this.role = role;
        this.joinDate = joinDate;
        this.avatarUrl = avatarUrl;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getRole() { return role; }
    public String getJoinDate() { return joinDate; }
    public String getAvatarUrl() { return avatarUrl; }

    // role can be changed by adminn
    public void setRole(String role) { this.role = role; }
}
