package com.example.demo.dto;


// 5. DTOs
public class RegistrationRequest {
    private String username;
    private String email;
    private String displayName;

    // Constructors, getters, and setters
    public RegistrationRequest() {}

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
}

