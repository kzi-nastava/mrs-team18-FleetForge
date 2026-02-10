package com.team18.FleetForge.dto.ride.view;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Basic DTO for listing all active rides.
 * Used in list view - fetched once on page load.
 * Does NOT include live location data.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActiveRideDTO {

    private Long rideId;

    private String driverFirstName;
    private String driverLastName;
    private String driverProfileImage;

    private String startAddress;
    private String endAddress;

    private LocalDateTime startTime;

    private Boolean panicActivated;

    private Integer passengerCount;
}