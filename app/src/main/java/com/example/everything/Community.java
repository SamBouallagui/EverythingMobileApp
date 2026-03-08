package com.example.everything;

import java.io.Serializable;
//serializable to pass data as bytes automatically between activities
public class Community implements Serializable {
    private String id;
    private String name;
    private String category;
    private String description;
    private int memberCount;
    private String imageUrl;
    private boolean isJoined;

    //Constructor

    public Community(String id, String name, String category, String description,int memberCount, String imageUrl, boolean isJoined){
        this.id=id;
        this.name=name;
        this.category=category;
        this.description=description;
        this.memberCount=memberCount;
        this.imageUrl=imageUrl;
        this.isJoined=isJoined;
    }
    public String getId(){return id;}
    public String getName(){return name;}
    public String getCategory(){return category;}
    public String getDescription(){return description;}
    public int getMemberCount(){return memberCount;}
    public String getImageUrl(){return imageUrl;}
    public boolean isJoined(){return isJoined;}
    public void setJoined(boolean joined){isJoined=joined;}
}
