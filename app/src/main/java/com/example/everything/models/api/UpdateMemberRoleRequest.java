package com.example.everything.models.api;

public class UpdateMemberRoleRequest {
    private String role;

    public UpdateMemberRoleRequest(String role) {
        this.role = role;
    }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
