package com.team18.FleetForge.dto.ride.lifecycle;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.team18.FleetForge.model.GeoPoint;
import com.team18.FleetForge.model.enums.VehicleType;
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
    private ArrayList<GeoPoint> coordinates;
    private int passengerNumber;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime rideTime;
    private boolean rideNow;
    private ArrayList<String> passengerEmails;
    private VehicleType vehicleType;
    private boolean babySeat;
    private boolean petFriendly;
}
