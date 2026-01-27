package com.team18.FleetForge.service;

import com.team18.FleetForge.dto.driver.DriverInfoDTO;
import com.team18.FleetForge.dto.ride.reports.InconsistencyReportDTO;
import com.team18.FleetForge.dto.ride.reports.InconsistencyReportResponseDTO;
import com.team18.FleetForge.dto.ride.view.DriverLocationUpdateRequestDTO;
import com.team18.FleetForge.dto.ride.view.DriverLocationUpdateResponseDTO;
import com.team18.FleetForge.dto.ride.view.RideTrackingDTO;
import com.team18.FleetForge.model.GeoPoint;
import com.team18.FleetForge.model.enums.Role;
import com.team18.FleetForge.model.ride.InconsistencyReport;
import com.team18.FleetForge.model.ride.Ride;
import com.team18.FleetForge.model.ride.WayPoint;
import com.team18.FleetForge.model.users.Driver;
import com.team18.FleetForge.model.users.Passenger;
import com.team18.FleetForge.repository.DriverRepository;
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
    private final DriverRepository driverRepository;
    private final InconsistencyReportRepository inconsistencyReportRepository;


    public RideTrackingDTO getActiveRideForUser(Long userId, Role role) {

        LocalDateTime fiveMinutesAgo = LocalDateTime.now().minusMinutes(5);
        List<Ride> activeRides;
        if (role.equals(Role.ROLE_DRIVER)) {
            activeRides = rideRepository.findActiveRidesByDriverId(userId);
        } else if (role.equals(Role.ROLE_PASSENGER)) {
            activeRides = rideRepository.findActiveRidesByPassengerId(userId, fiveMinutesAgo);
        } else {
            throw new IllegalArgumentException("Invalid role: " + role);
        }

        LocalDateTime tenMinutesFromNow = LocalDateTime.now().plusMinutes(10);

        activeRides = activeRides.stream()
                .filter(ride -> ride.getStartTime().isBefore(tenMinutesFromNow))
                .collect(Collectors.toList());

        if (activeRides.isEmpty()) {
            throw new RuntimeException("No active ride found for user ID: " + userId);
        }

        activeRides.sort((r1, r2) -> r1.getStartTime().compareTo(r2.getStartTime()));
        Ride ride = activeRides.get(0);

        return buildRideTrackingDTO(ride);
    }



    @Transactional
    public DriverLocationUpdateResponseDTO updateDriverLocation(
            Long driverId,
            DriverLocationUpdateRequestDTO request) {
        log.info("Updating location for driver ID: {}", driverId);

        List<Ride> activeRides = rideRepository.findActiveRidesByDriverId(driverId);

        if (activeRides.isEmpty()) {
            throw new RuntimeException("No active ride found for driver ID: " + driverId);
        }

        activeRides.sort((r1, r2) -> r1.getStartTime().compareTo(r2.getStartTime()));
        Ride ride = activeRides.get(0);
        Driver driver = ride.getDriver();
        GeoPoint newLocation = new GeoPoint(
                request.getCurrentLocation().getLatitude(),
                request.getCurrentLocation().getLongitude()
        );
        driver.setCurrentLocation(newLocation);

        driverRepository.save(driver);

        return DriverLocationUpdateResponseDTO.builder()
                .message("Driver location updated successfully")
                .updatedAt(LocalDateTime.now())
                .build();
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
        Passenger passenger = ride.getPassenger();

        return RideTrackingDTO.builder()
                .rideId(ride.getId())
                .status(ride.getStatus().name())
                .currentLocation(driver.getCurrentLocation())
                .estimatedArrivalMinutes(null)
                .route(buildRouteInfo(ride))
                .driver(buildDriverInfo(driver))
                .passenger(buildPassengerInfo(passenger))
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

    private RideTrackingDTO.PassengerInfoDTO buildPassengerInfo(Passenger passenger) {
        return RideTrackingDTO.PassengerInfoDTO.builder()
                .id(passenger.getId())
                .firstName(passenger.getFirstName())
                .lastName(passenger.getLastName())
                .phoneNumber(passenger.getPhoneNumber())
                .profileImage(passenger.getProfilePicture())
                .build();
    }

}