package com.ognjen.fleetforge.dtos.ride;

public class CancellationRequest {
    private String reason;

    public CancellationRequest(String reason) {
        this.reason = reason;
    }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}