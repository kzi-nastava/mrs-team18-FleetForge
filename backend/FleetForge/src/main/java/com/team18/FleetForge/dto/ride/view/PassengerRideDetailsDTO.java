package com.team18.FleetForge.dto.ride.view;

import com.team18.FleetForge.dto.driver.DriverInfoDTO;
import com.team18.FleetForge.dto.ride.reports.InconsistencyReportResponseDTO;
import com.team18.FleetForge.model.enums.RideStatus;
import com.team18.FleetForge.model.enums.VehicleType;
import com.team18.FleetForge.model.ride.GeoPoint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PassengerRideDetailsDTO {

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

    // real tracked route
    private List<GeoPoint> realRoute;

    private boolean hasInconsistencies;
    private List<InconsistencyReportResponseDTO> inconsistencies;
}
