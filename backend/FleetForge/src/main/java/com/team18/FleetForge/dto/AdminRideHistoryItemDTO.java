package com.team18.FleetForge.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AdminRideHistoryItemDTO {
    private Long rideId;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    private String startAddress;
    private String destinationAddress;

    private UserSummaryDTO driver;
    private UserSummaryDTO passenger;

}
