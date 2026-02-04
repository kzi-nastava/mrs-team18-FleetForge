package com.ognjen.fleetforge.dtos;

public class PasswordResetRequestDTO {
    private String email;

    public PasswordResetRequestDTO(String email) {
        this.email = email;
    }

    // Getter and Setter
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}