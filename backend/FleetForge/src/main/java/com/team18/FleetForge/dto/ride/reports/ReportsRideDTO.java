package com.team18.FleetForge.dto.ride.reports;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportsRideDTO {
    private Long id;
    private double totalDistance;
    private double totalCost;
}
