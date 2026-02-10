package com.team18.FleetForge.dto.ride.view;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class ScheduledRideDTO {
    private Long id;
    private String pickup;
    private String dropoff;
    private LocalDateTime scheduledTime;
    private double estimatedCost;
    private String status;
}
