package com.ognjen.fleetforge.dtos.ride;

public class RideReviewRequestDTO {
    private Integer vehicleRating;
    private Integer driverRating;
    private String comment;

    public RideReviewRequestDTO() {
    }

    public RideReviewRequestDTO(Integer vehicleRating, Integer driverRating, String comment) {
        this.vehicleRating = vehicleRating;
        this.driverRating = driverRating;
        this.comment = comment;
    }

    public Integer getVehicleRating() {
        return vehicleRating;
    }

    public void setVehicleRating(Integer vehicleRating) {
        this.vehicleRating = vehicleRating;
    }

    public Integer getDriverRating() {
        return driverRating;
    }

    public void setDriverRating(Integer driverRating) {
        this.driverRating = driverRating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}