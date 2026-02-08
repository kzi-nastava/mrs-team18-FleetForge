package com.team18.FleetForge.dto.ride.view;

import com.team18.FleetForge.dto.UserSummaryDTO;
import com.team18.FleetForge.dto.driver.DriverInfoDTO;
import com.team18.FleetForge.dto.ride.reports.InconsistencyReportResponseDTO;
import com.team18.FleetForge.dto.ride.review.RideRatingDTO;
import com.team18.FleetForge.model.enums.RideStatus;
import com.team18.FleetForge.model.enums.VehicleType;
import com.team18.FleetForge.model.ride.GeoPoint;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class AdminRideDetailsDTO {

    private Long id;
    private String startAddress;
    private String endAddress;
    private GeoPoint startLocation;
    private GeoPoint endLocation;
    private List<GeoPoint> wayPoints;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Double totalDistance;
    private Double estimatedDuration;
    private Double totalCost;
    private RideStatus status;
    private VehicleType vehicleType;
    private boolean petFriendly;
    private boolean babySeat;

    private DriverInfoDTO driver;

    private UserSummaryDTO mainPassenger; // main
    private List<UserSummaryDTO> linkedPassengers; // linked

    private boolean hasInconsistencies;
    private List<InconsistencyReportResponseDTO> inconsistencies;

    private String cancelledBy; // DRIVER or PASSENGER
    private String cancellationReason; // optional

    private RideRatingDTO ratings; // vehicle + driver ratings

    private List<GeoPoint> realRoute;
}
