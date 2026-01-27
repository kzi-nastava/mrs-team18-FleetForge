package com.team18.FleetForge.service;

import com.team18.FleetForge.dto.driver.DriverInfoDTO;
import com.team18.FleetForge.dto.ride.lifecycle.FinishRideRequestDTO;
import com.team18.FleetForge.dto.ride.lifecycle.FinishRideResponseDTO;
import com.team18.FleetForge.dto.ride.view.RideTrackingDTO;
import com.team18.FleetForge.model.enums.RideStatus;
import com.team18.FleetForge.model.enums.VehicleType;
import com.team18.FleetForge.model.ride.Ride;
import com.team18.FleetForge.model.ride.RideLocation;
import com.team18.FleetForge.model.ride.WayPoint;
import com.team18.FleetForge.model.users.Driver;
import com.team18.FleetForge.model.users.Passenger;
import com.team18.FleetForge.repository.RideLocationRepository;
import com.team18.FleetForge.repository.RideRepository;
import com.team18.FleetForge.repository.DriverRepository;
import com.team18.FleetForge.util.GeoUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RideFinishService {

    private final RideRepository rideRepository;
    private final DriverRepository driverRepository;
    private final EmailService emailService;
    private final RideLocationRepository rideLocationRepository;
    private final PriceCalculationService priceCalculationService;

    @Transactional
    public FinishRideResponseDTO finishRide(FinishRideRequestDTO request) {
        Ride ride = rideRepository.findById(request.getRideId())
                .orElseThrow(() -> new RuntimeException("Ride not found with id: " + request.getRideId()));

//        if (ride.getStatus() != RideStatus.IN_PROGRESS) {
//            throw new RuntimeException("Cannot finish ride. Ride is not in progress. Current status: " + ride.getStatus());
//        }

        ride.setStatus(RideStatus.COMPLETED);
        ride.setEndTime(LocalDateTime.now());


        System.out.println("FFLOG: Ride ID: " + ride.getId());
        LocalDateTime startTime = ride.getStartTime();


        List<RideLocation> locations =
                rideLocationRepository
                        .findByRideAndRecordedAtAfterOrderByRecordedAtAsc(
                                ride,
                                startTime
                        );

        double totalDistanceKm = 0.0;


        System.out.println("FFLOG: RideLocation ID: " + locations.get(0).getId());
        System.out.println("FFLOG: Number of RideLocations: " + locations.size());

        for (int i = 1; i < locations.size(); i++) {

            RideLocation prev = locations.get(i - 1);
            RideLocation curr = locations.get(i);

            totalDistanceKm += GeoUtils.distanceKm(
                    prev.getLatitude(), prev.getLongitude(),
                    curr.getLatitude(), curr.getLongitude()
            );
        }

        double roundedDistance = BigDecimal.valueOf(totalDistanceKm)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();

        ride.setTotalDistance(roundedDistance);

        VehicleType vehicleType = ride.getVehicleType();

        double totalCost = priceCalculationService.calculatePrice(
                totalDistanceKm,
                vehicleType
        );

        double roundedCost = BigDecimal.valueOf(totalCost)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();

        ride.setTotalCost(roundedCost);

        rideRepository.save(ride);

        Driver driver = ride.getDriver();
        List<Ride> activeRides = rideRepository.findAllByDriverAndStatus(driver, RideStatus.ACCEPTED);
        activeRides.sort((r1, r2) -> r1.getStartTime().compareTo(r2.getStartTime()));


        LocalDateTime now = LocalDateTime.now();
        LocalDateTime tenMinutesFromNow = now.plusMinutes(10);

        RideTrackingDTO nextScheduledRide = null;

        if (!activeRides.isEmpty()) {
            Ride nextRide = activeRides.get(0);
            Passenger passenger = nextRide.getPassenger();
            boolean isWithinTenMinutes = nextRide.getStartTime().isAfter(now)
                    && nextRide.getStartTime().isBefore(tenMinutesFromNow);

            if (isWithinTenMinutes) {
                nextScheduledRide = RideTrackingDTO.builder()
                        .rideId(nextRide.getId())
                        .status(nextRide.getStatus().name())
                        .currentLocation(driver.getCurrentLocation())
                        .estimatedArrivalMinutes(null)
                        .route(buildRouteInfo(nextRide))
                        .driver(buildDriverInfo(driver))
                        .passenger(buildPassengerInfo(passenger))
                        .panicActivated(nextRide.getPanicActivated() != null && nextRide.getPanicActivated())
                        .build();

            } else {
                driver.setAvailable(true);
            }
        } else {
            driver.setAvailable(true);
        }


        driverRepository.save(driver);

        sendRideCompletionEmails(ride);

        return FinishRideResponseDTO.builder()
                .rideId(ride.getId())
                .status(RideStatus.COMPLETED)
                .endTime(ride.getEndTime())
                .totalCost(ride.getTotalCost())
                .message("Ride completed successfully")
                .driverAvailable(driver.isAvailable())
                .nextRide(nextScheduledRide)
                .build();
    }

    private void sendRideCompletionEmails(Ride ride) {
        List<Passenger> allPassengers = new ArrayList<>();
        allPassengers.add(ride.getPassenger());
        if (ride.getLinkedPassengers() != null && !ride.getLinkedPassengers().isEmpty()) {
            allPassengers.addAll(ride.getLinkedPassengers());
        }

        for (Passenger passenger : allPassengers) {
            String subject = "Ride Completed - FleetForge";
            String body = buildCompletionEmailBody(ride, passenger);

            try {
                emailService.sendEmail(passenger.getEmail(), subject, body);
            } catch (Exception e) {
                System.err.println("Failed to send completion email to: " + passenger.getEmail());
            }
        }
    }

    private String buildCompletionEmailBody(Ride ride, Passenger passenger) {
        return String.format(
                "Dear %s %s,\n\n" +
                        "Your ride has been completed successfully!\n\n" +
                        "Ride Details:\n" +
                        "- From: %s\n" +
                        "- To: %s\n" +
                        "- Start Time: %s\n" +
                        "- End Time: %s\n" +
                        "- Total Cost: %.2f RSD\n\n" +
                        "Thank you for using FleetForge!\n\n" +
                        "You can now rate your ride and driver through the application.\n\n" +
                        "Best regards,\n" +
                        "FleetForge Team",
                passenger.getFirstName(),
                passenger.getLastName(),
                ride.getStartAddress(),
                ride.getEndAddress(),
                ride.getStartTime(),
                ride.getEndTime(),
                ride.getTotalCost()
        );
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