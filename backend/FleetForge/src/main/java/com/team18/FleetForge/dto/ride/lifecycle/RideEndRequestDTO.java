package com.team18.FleetForge.dto.ride.lifecycle;

import com.team18.FleetForge.model.GeoPoint;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RideEndRequestDTO {
    private GeoPoint stopLocation;
    private LocalDateTime endTime;
}
