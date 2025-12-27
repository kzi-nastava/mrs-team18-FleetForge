package com.team18.FleetForge.dto.vehicle;

import com.team18.FleetForge.model.GeoPoint;
import com.team18.FleetForge.model.enums.VehicleType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehicleLocationDTO {
    private Long vehicleId;
    private String model;
    private VehicleType vehicleType;
    private GeoPoint currentLocation;
    private Boolean isAvailable;
    private Boolean isActive;
}