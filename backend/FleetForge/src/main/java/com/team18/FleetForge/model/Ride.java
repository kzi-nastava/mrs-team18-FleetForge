package com.team18.FleetForge.model;

import com.team18.FleetForge.model.enums.RideStatus;
import com.team18.FleetForge.model.enums.VehicleType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ride {
    private Long id;
    private Long driverId;
    private Long passengerId;
    private Route route;
    private int passengerNumber;
    private LocalDateTime rideTime;
    private boolean rideNow;
    private ArrayList<String> passengerEmails;
    private VehicleType vehicleType;
    private boolean babySeat;
    private boolean petFriendly;
    private boolean panicActivated;
    private LocalDateTime startTime;
    private LocalDateTime stopTime;
    private double totalPrice;
    private RideStatus rideStatus;
    private String cancellationReason;
    private GeoPoint stopLocation;
}
