package com.team18.FleetForge.dto.ride.lifecycle;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompletedRideDTO {
    private String passengerName;
    private Long id;
    private String pickupAddress;
    private String dropoffAddress;
    private String rideDate;
    private Double totalCost;
    private String cancelledBy; // "driver", "passenger", or null
    private Boolean panicActivation;
    private Integer feedback; // 0-5 stars, 0 means waiting
    private List<String> linkedPassengers;
    private List<Double> pickupCoords;
    private List<Double> dropoffCoords;
    private List<List<Double>> waypoints;
}
