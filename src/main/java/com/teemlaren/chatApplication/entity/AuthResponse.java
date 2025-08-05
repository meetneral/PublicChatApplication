package com.teemlaren.chatApplication.entity;



public class AuthResponse {
    private String jwt;

    // Constructors
    public AuthResponse() {}

    public AuthResponse(String jwt) {
        this.jwt = jwt;
    }

    // Getter and Setter
    public String getJwt() {
        return jwt;
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }
}
