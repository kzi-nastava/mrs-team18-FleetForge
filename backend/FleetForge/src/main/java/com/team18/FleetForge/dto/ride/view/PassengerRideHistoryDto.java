package com.team18.FleetForge.dto.ride.view;

import com.team18.FleetForge.model.ride.GeoPoint;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@AllArgsConstructor
@Data
public class PassengerRideHistoryDto {

    private Long rideId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private GeoPoint startLocation;
    private GeoPoint endLocation;
    private Integer vehicleRating;
    private Integer driverRating;

    public Double getAverageReview() {
        if (vehicleRating == null || driverRating == null) return null;
        return (vehicleRating + driverRating) / 2.0;
    }
}
