package com.ognjen.fleetforge.dtos.driver;

public class DriverSessionRequestDTO {
    private int sessionId;

    public DriverSessionRequestDTO(int sessionId) {
        this.sessionId = sessionId;
    }

    public int getSessionId() { return sessionId; }
    public void setSessionId(int sessionId) { this.sessionId = sessionId; }
}
