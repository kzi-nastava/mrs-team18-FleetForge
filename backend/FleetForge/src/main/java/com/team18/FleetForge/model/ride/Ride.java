package com.team18.FleetForge.model.ride;

import com.team18.FleetForge.model.GeoPoint;
import com.team18.FleetForge.model.users.Driver;
import com.team18.FleetForge.model.users.Passenger;
import com.team18.FleetForge.model.enums.PanicInitiator;
import com.team18.FleetForge.model.enums.RideCancellationRole;
import com.team18.FleetForge.model.enums.RideStatus;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ride {
    private Long id;
    private GeoPoint startLocation;
    private GeoPoint endLocation;

    private String startAddress;
    private String endAddress;
    private List<WayPoint> wayPoints = new ArrayList<>();

    private Double totalDistance; // in kilometers
    private Double estimatedDuration; // in minutes
    private Double totalCost;

    private Driver driver;
    private Passenger passenger;
    private List<Passenger> linkedPassengers = new ArrayList<>();

    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime cancelledAt;

    private RideStatus status;
    private String cancellationReason;
    private RideCancellationRole cancelledBy;

    private Boolean panicActivated = false;
    private LocalDateTime panicActivatedAt;
    private PanicInitiator panicInitiator;
}
