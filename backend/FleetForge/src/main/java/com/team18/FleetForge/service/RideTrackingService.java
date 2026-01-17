package com.team18.FleetForge.service;

import com.team18.FleetForge.dto.driver.DriverInfoDTO;
import com.team18.FleetForge.dto.ride.reports.InconsistencyReportDTO;
import com.team18.FleetForge.dto.ride.reports.InconsistencyReportResponseDTO;
import com.team18.FleetForge.dto.ride.view.RideTrackingDTO;
import com.team18.FleetForge.model.ride.InconsistencyReport;
import com.team18.FleetForge.model.enums.RideStatus;
import com.team18.FleetForge.model.ride.Ride;
import com.team18.FleetForge.model.ride.WayPoint;
import com.team18.FleetForge.model.users.Driver;
import com.team18.FleetForge.model.users.Passenger;
import com.team18.FleetForge.repository.InconsistencyReportRepository;
import com.team18.FleetForge.repository.PassengerRepository;
import com.team18.FleetForge.repository.RideRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class RideTrackingService {

    private final RideRepository rideRepository;
    private final PassengerRepository passengerRepository;
    private final InconsistencyReportRepository inconsistencyReportRepository;


    public RideTrackingDTO getActiveRideForPassenger(Long passengerId) {
        log.info("Fetching active ride for passenger ID: {}", passengerId);

        Ride ride = rideRepository.findActiveRideByPassengerId(passengerId, RideStatus.IN_PROGRESS)
                .orElse(null);

        if (ride == null) {
            log.info("No active ride found for passenger ID: {}", passengerId);
            return null;
        }

        return buildRideTrackingDTO(ride);
    }

    @Transactional
    public InconsistencyReportResponseDTO reportInconsistency(
            Long rideId,
            InconsistencyReportDTO request,
            Long reporterId
    ) {
        log.info("Creating inconsistency report for ride ID: {} by passenger ID: {}", rideId, reporterId);

        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new RuntimeException("Ride not found with ID: " + rideId));

        Passenger reporter = passengerRepository.findById(reporterId)
                .orElseThrow(() -> new RuntimeException("Passenger not found with ID: " + reporterId));

        InconsistencyReport report = InconsistencyReport.builder()
                .ride(ride)
                .reporter(reporter)
                .description(request.getComment())
                .reportedAt(LocalDateTime.now())
                .reportLocation(request.getCurrentLocation())
                .build();

        report = inconsistencyReportRepository.save(report);

        log.info("Inconsistency report created with ID: {}", report.getId());

        return InconsistencyReportResponseDTO.builder()
                .reportId(report.getId())
                .message("Inconsistency report submitted successfully")
                .reportedAt(report.getReportedAt())
                .build();
    }


    private RideTrackingDTO buildRideTrackingDTO(Ride ride) {
        Driver driver = ride.getDriver();

        return RideTrackingDTO.builder()
                .rideId(ride.getId())
                .status(ride.getStatus().name())
                .currentLocation(driver.getCurrentLocation())
                .estimatedArrivalMinutes(null)
                .route(buildRouteInfo(ride))
                .driver(buildDriverInfo(driver))
                .panicActivated(ride.getPanicActivated() != null && ride.getPanicActivated())
                .build();
    }

    private RideTrackingDTO.RouteInfoDTO buildRouteInfo(Ride ride) {
        List<RideTrackingDTO.WaypointDTO> waypointDTOs = ride.getWayPoints().stream()
                .map(this::buildWaypointDTO)
                .collect(Collectors.toList());

        return RideTrackingDTO.RouteInfoDTO.builder()
                .startLocation(ride.getStartLocation())
                .startAddress(ride.getStartAddress())
                .endLocation(ride.getEndLocation())
                .endAddress(ride.getEndAddress())
                .waypoints(waypointDTOs)
                .totalDistanceKm(ride.getTotalDistance())
                .build();
    }

    private RideTrackingDTO.WaypointDTO buildWaypointDTO(WayPoint wayPoint) {
        return RideTrackingDTO.WaypointDTO.builder()
                .location(wayPoint.getLocation())
                .address(wayPoint.getAddress())
                .order(wayPoint.getOrderIndex())
                .build();
    }


    private DriverInfoDTO buildDriverInfo(Driver driver) {
        return DriverInfoDTO.builder()
                .id(driver.getId())
                .firstName(driver.getFirstName())
                .lastName(driver.getLastName())
                .phoneNumber(driver.getPhoneNumber())
                .profileImage(driver.getProfilePicture())
                .build();
    }

}