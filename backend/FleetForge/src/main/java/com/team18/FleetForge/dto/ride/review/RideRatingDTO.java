package com.team18.FleetForge.dto.ride.review;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RideRatingDTO {
    private Integer driverRating;
    private Integer vehicleRating;


}