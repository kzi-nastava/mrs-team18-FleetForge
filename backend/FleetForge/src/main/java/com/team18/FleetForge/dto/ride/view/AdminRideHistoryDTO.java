package com.team18.FleetForge.dto.ride.view;

import com.team18.FleetForge.model.enums.RideStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class AdminRideHistoryDTO {

    private Long rideId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String startAddress;
    private String endAddress;
    private boolean panicActivated;
    private RideStatus status;
}
