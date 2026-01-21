package com.team18.FleetForge.dto.ride.lifecycle;

import com.team18.FleetForge.model.enums.RideStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FinishRideResponseDTO {

    private Long rideId;
    private RideStatus status;
    private LocalDateTime endTime;
    private Double totalCost;
    private String message;

    // Driver availability info
    private boolean driverAvailable;
    private Long nextScheduledRideId;
}