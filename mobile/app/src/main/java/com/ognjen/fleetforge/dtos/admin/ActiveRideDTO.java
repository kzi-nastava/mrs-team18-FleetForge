package com.ognjen.fleetforge.dtos.admin;

public class ActiveRideDTO {
    private Long rideId;
    private String driverFirstName;
    private String driverLastName;
    private String driverProfileImage;
    private String startAddress;
    private String endAddress;
    private String startTime;
    private Boolean panicActivated;
    private Integer passengerCount;

    public ActiveRideDTO() {
    }

    public Long getRideId() {
        return rideId;
    }

    public void setRideId(Long rideId) {
        this.rideId = rideId;
    }

    public String getDriverFirstName() {
        return driverFirstName;
    }

    public void setDriverFirstName(String driverFirstName) {
        this.driverFirstName = driverFirstName;
    }

    public String getDriverLastName() {
        return driverLastName;
    }

    public void setDriverLastName(String driverLastName) {
        this.driverLastName = driverLastName;
    }

    public String getDriverProfileImage() {
        return driverProfileImage;
    }

    public void setDriverProfileImage(String driverProfileImage) {
        this.driverProfileImage = driverProfileImage;
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

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public Boolean getPanicActivated() {
        return panicActivated;
    }

    public void setPanicActivated(Boolean panicActivated) {
        this.panicActivated = panicActivated;
    }

    public Integer getPassengerCount() {
        return passengerCount;
    }

    public void setPassengerCount(Integer passengerCount) {
        this.passengerCount = passengerCount;
    }
}

