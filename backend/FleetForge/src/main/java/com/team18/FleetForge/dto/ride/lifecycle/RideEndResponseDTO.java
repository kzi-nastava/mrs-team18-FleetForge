package com.team18.FleetForge.dto.ride.lifecycle;

import com.team18.FleetForge.model.GeoPoint;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RideEndResponseDTO {

    private GeoPoint finalDestination;
    private double finalPrice;
}
