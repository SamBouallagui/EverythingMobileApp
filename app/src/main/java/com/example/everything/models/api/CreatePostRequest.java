package com.example.everything.models.api;

public class CreatePostRequest {
    private String content;

    public CreatePostRequest(String content) {
        this.content = content;
    }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}
