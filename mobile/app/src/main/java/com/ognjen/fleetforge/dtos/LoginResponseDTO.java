package com.ognjen.fleetforge.dtos;

public class LoginResponseDTO {
    private String token;
    private String role;
    private String loggedInAt;

    public LoginResponseDTO() {
    }

    public LoginResponseDTO(String token, String role, String loggedInAt) {
        this.token = token;
        this.role = role;
        this.loggedInAt = loggedInAt;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getLoggedInAt() {
        return loggedInAt;
    }

    public void setLoggedInAt(String loggedInAt) {
        this.loggedInAt = loggedInAt;
    }
}