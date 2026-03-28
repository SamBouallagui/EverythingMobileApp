package com.example.everything.models.api;

public class CreateCommentRequest {
    private String content;

    public CreateCommentRequest(String content) {
        this.content = content;
    }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}
