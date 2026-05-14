package com.example.everything.models.api;

import java.util.List;

public class PostDto {
    private int id;
    private int communityId;
    private int authorId;
    private String content;
    private String createdAt;
    private boolean isLikedByMe;
    private int likeCount;
    private String authorUsername;
    private int commentCount;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getCommunityId() { return communityId; }
    public void setCommunityId(int communityId) { this.communityId = communityId; }

    public int getAuthorId() { return authorId; }
    public void setAuthorId(int authorId) { this.authorId = authorId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    // Champs de la réponse API
    public boolean isLiked() { return isLikedByMe; }
    public void setLiked(boolean liked) { this.isLikedByMe = liked; }
    
    public boolean isLikedByMe() { return isLikedByMe; }
    public void setLikedByMe(boolean likedByMe) { this.isLikedByMe = likedByMe; }

    public int getLikeCount() { return likeCount; }
    public void setLikeCount(int likeCount) { this.likeCount = likeCount; }

    public String getAuthorName() { return authorUsername; } // Map to existing method
    public void setAuthorName(String authorName) { this.authorUsername = authorName; }
    
    public String getAuthorUsername() { return authorUsername; }
    public void setAuthorUsername(String authorUsername) { this.authorUsername = authorUsername; }

    public int getCommentCount() { return commentCount; }
    public void setCommentCount(int commentCount) { this.commentCount = commentCount; }
}
