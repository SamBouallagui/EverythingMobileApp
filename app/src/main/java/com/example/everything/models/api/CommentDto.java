package com.example.everything.models.api;

public class CommentDto {
    private int id;
    private int postId;
    private int authorId;
    private String content;
    private String createdAt;
    private String authorName;
    private String authorRole;

    public CommentDto() {}

    // Getters
    public int getId() { return id; }
    public int getPostId() { return postId; }
    public int getAuthorId() { return authorId; }
    public String getContent() { return content; }
    public String getCreatedAt() { return createdAt; }
    public String getAuthorName() { return authorName; }
    public String getAuthorRole() { return authorRole; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setPostId(int postId) { this.postId = postId; }
    public void setAuthorId(int authorId) { this.authorId = authorId; }
    public void setContent(String content) { this.content = content; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    public void setAuthorName(String authorName) { this.authorName = authorName; }
    public void setAuthorRole(String authorRole) { this.authorRole = authorRole; }
}
