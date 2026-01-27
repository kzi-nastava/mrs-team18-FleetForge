package com.team18.FleetForge.dto.ride.lifecycle;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpcomingRideDTO {
    private Long rideId;
    private String passengerName;
    private String passengerImage;
    private String startAddress;
    private String endAddress;
    private LocalDateTime startTime;
    private Double cost;
}
