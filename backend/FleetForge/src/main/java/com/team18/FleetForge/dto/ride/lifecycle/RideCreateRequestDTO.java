package com.team18.FleetForge.dto.ride.lifecycle;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.team18.FleetForge.dto.ride.routes.WayPointDTO;
import com.team18.FleetForge.model.GeoPoint;
import com.team18.FleetForge.model.enums.VehicleType;
import com.team18.FleetForge.model.ride.WayPoint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class RideCreateRequestDTO {
    private ArrayList<WayPointDTO> coordinates;
    private int passengerNumber;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime rideTime;
    private boolean rideNow;
    private ArrayList<String> passengerEmails;
    private VehicleType vehicleType;
    private boolean babySeat;
    private boolean petFriendly;
    private String startAddress;
    private String endAddress;
    private Double totalDistance;
    private Double estimatedDuration;
}
