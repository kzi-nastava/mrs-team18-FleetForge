package com.team18.FleetForge.dto.ride.review;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RideReviewResponseDTO {

    private Long rideId;
    private Integer vehicleRating;
    private Integer driverRating;
    private String comment;
    private LocalDateTime reviewedAt;
    private String message;
}