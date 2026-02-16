package com.ognjen.fleetforge.dtos.admin;

public class AdminRideHistoryDto {
    private Long rideId;
    private String startTime;
    private String endTime;
    private String startAddress;
    private String endAddress;
    private String status;
    private Boolean panicActivated;

    public AdminRideHistoryDto() {}

    // Getters and Setters
    public Long getRideId() {
        return rideId;
    }

    public void setRideId(Long rideId) {
        this.rideId = rideId;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getStartAddress() {
        return startAddress;
    }

    public void setStartAddress(String startAddress) {
        this.startAddress = startAddress;
    }

    public String getEndAddress() {
        return endAddress;
    }

    public void setEndAddress(String endAddress) {
        this.endAddress = endAddress;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Boolean getPanicActivated() {
        return panicActivated;
    }

    public void setPanicActivated(Boolean panicActivated) {
        this.panicActivated = panicActivated;
    }
}