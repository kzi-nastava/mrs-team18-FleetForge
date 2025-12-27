package com.team18.FleetForge.dto.ride.lifecycle;

import com.team18.FleetForge.model.enums.RideStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RideCreateResponseDTO {
    private Long rideId;
    private RideStatus status;
    private double estimatedPrice;
    private String message;
}
