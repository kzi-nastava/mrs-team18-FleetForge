package com.team18.FleetForge.dto.ride.lifecycle;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.team18.FleetForge.dto.ride.routes.WayPointDTO;
import com.team18.FleetForge.model.GeoPoint;
import com.team18.FleetForge.model.enums.VehicleType;
import com.team18.FleetForge.model.ride.WayPoint;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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
    @NotEmpty
    private ArrayList<WayPointDTO> coordinates;
    @NotNull
    @Min(1)
    private int passengerNumber;
    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime rideTime;
    private boolean rideNow;
    private ArrayList<String> passengerEmails;
    @NotNull
    private VehicleType vehicleType;
    @NotNull
    private boolean babySeat;
    @NotNull
    private boolean petFriendly;
    @NotNull
    private String startAddress;
    @NotNull
    private String endAddress;
    @NotNull
    private Double totalDistance;
    @NotNull
    private Double estimatedDuration;
}
