package com.example.everything;

public class Comment {
    private String id;
    private String postId;
    private String authorId;
    private String authorName;
    private String authorRole;
    private String content;
    private String createdAt;
    private String timeAgo;

    public Comment() {}

    public Comment(String id, String postId, String authorId, String authorName, String authorRole, String content, String createdAt) {
        this.id = id;
        this.postId = postId;
        this.authorId = authorId;
        this.authorName = authorName;
        this.authorRole = authorRole;
        this.content = content;
        this.createdAt = createdAt;
    }

    public Comment(String id, String postId, String authorId, String authorName, String authorRole, String content, String createdAt, String timeAgo) {
        this.id = id;
        this.postId = postId;
        this.authorId = authorId;
        this.authorName = authorName;
        this.authorRole = authorRole;
        this.content = content;
        this.createdAt = createdAt;
        this.timeAgo = timeAgo;
    }

    // Getters
    public String getId() { return id; }
    public String getPostId() { return postId; }
    public String getAuthorId() { return authorId; }
    public String getAuthorName() { return authorName; }
    public String getAuthorRole() { return authorRole; }
    public String getContent() { return content; }
    public String getCreatedAt() { return createdAt; }
    public String getTimeAgo() { return timeAgo; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setPostId(String postId) { this.postId = postId; }
    public void setAuthorId(String authorId) { this.authorId = authorId; }
    public void setAuthorName(String authorName) { this.authorName = authorName; }
    public void setAuthorRole(String authorRole) { this.authorRole = authorRole; }
    public void setContent(String content) { this.content = content; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    public void setTimeAgo(String timeAgo) { this.timeAgo = timeAgo; }
}
