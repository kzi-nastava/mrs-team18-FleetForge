package com.team18.FleetForge.dto.ride.view;

import com.team18.FleetForge.model.ride.GeoPoint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DriverLocationUpdateRequestDTO {
    private GeoPoint currentLocation;
}
