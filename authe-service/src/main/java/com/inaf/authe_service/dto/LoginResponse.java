package com.inaf.authe_service.dto;

public class LoginResponse {

    private String token;
    private String username;
    private String email;
    private String tokenType = "Bearer";

    // Constructeurs
    public LoginResponse() {}

    public LoginResponse(String token, String username, String email) {
        this.token = token;
        this.username = username;
        this.email = email;
    }

    // Getters et Setters
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTokenType() { return tokenType; }
    public void setTokenType(String tokenType) { this.tokenType = tokenType; }
}