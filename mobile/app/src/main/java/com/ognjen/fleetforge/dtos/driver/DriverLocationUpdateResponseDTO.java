package com.ognjen.fleetforge.dtos.driver;

public class DriverLocationUpdateResponseDTO {
    private String message;
    private String updatedAt;  // Using String for simplicity with date parsing

    public DriverLocationUpdateResponseDTO() {
    }

    public DriverLocationUpdateResponseDTO(String message, String updatedAt) {
        this.message = message;
        this.updatedAt = updatedAt;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "DriverLocationUpdateResponseDTO{" +
                "message='" + message + '\'' +
                ", updatedAt='" + updatedAt + '\'' +
                '}';
    }
}