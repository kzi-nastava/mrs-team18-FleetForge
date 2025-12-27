package com.team18.FleetForge.model;

import com.team18.FleetForge.model.Users.Passenger;
import com.team18.FleetForge.model.ride.Ride;
import lombok.*;
import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InconsistencyReport {

    private Long id;
    private Ride ride;
    private Passenger reporter;
    private String description;
    private LocalDateTime reportedAt;
    private GeoPoint reportLocation;
}