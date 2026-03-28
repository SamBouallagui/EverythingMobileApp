package com.example.everything.models.api;

public class AuthResponse {
    private String token;
    private UserDto user;

    public String getToken() { return token; }
    public UserDto getUser() { return user; }

    public static class UserDto {
        private int id;
        private String username;
        private String email;
        private String role;

        public int getId() { return id; }
        public String getUsername() { return username; }
        public String getEmail() { return email; }
        public String getRole() { return role; }
    }
}