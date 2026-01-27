package com.team18.FleetForge.service;

import com.team18.FleetForge.dto.ride.lifecycle.CompletedRideDTO;
import com.team18.FleetForge.model.ride.Ride;
import com.team18.FleetForge.model.ride.RideReview;
import com.team18.FleetForge.model.ride.WayPoint;
import com.team18.FleetForge.model.users.Passenger;
import com.team18.FleetForge.repository.RideRepository;
import com.team18.FleetForge.repository.RideReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DriverHistoryService {

    private final RideRepository rideRepository;
    private final RideReviewRepository rideReviewRepository;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Transactional(readOnly = true)
    public List<CompletedRideDTO> getDriverRideHistory(Long driverId) {
        List<Ride> completedRides = rideRepository.findCompletedRidesByDriverId(driverId);

        return completedRides.stream()
                .map(this::buildCompletedRideDTO)
                .collect(Collectors.toList());
    }

    private CompletedRideDTO buildCompletedRideDTO(Ride ride) {
        Passenger mainPassenger = ride.getPassenger();
        String passengerName = mainPassenger.getFirstName() + " " + mainPassenger.getLastName();

        List<String> linkedPassengerNames = new ArrayList<>();
        if (ride.getLinkedPassengers() != null && !ride.getLinkedPassengers().isEmpty()) {
            linkedPassengerNames = ride.getLinkedPassengers().stream()
                    .map(passenger -> passenger.getFirstName() + " " + passenger.getLastName())
                    .collect(Collectors.toList());
        }

        Integer feedback = 0;
        Optional<RideReview> reviewOpt = rideReviewRepository.findByRideId(ride.getId());
        if (reviewOpt.isPresent()) {
            feedback = reviewOpt.get().getDriverRating();
        }

        List<Double> pickupCoords = null;
        if (ride.getStartLocation() != null) {
            pickupCoords = List.of(
                    ride.getStartLocation().getLatitude(),
                    ride.getStartLocation().getLongitude()
            );
        }

        List<Double> dropoffCoords = null;
        if (ride.getEndLocation() != null) {
            dropoffCoords = List.of(
                    ride.getEndLocation().getLatitude(),
                    ride.getEndLocation().getLongitude()
            );
        }

        List<List<Double>> waypoints = new ArrayList<>();
        if (ride.getWayPoints() != null && !ride.getWayPoints().isEmpty()) {
            waypoints = ride.getWayPoints().stream()
                    .map(wayPoint -> List.of(
                            wayPoint.getLocation().getLatitude(),
                            wayPoint.getLocation().getLongitude()
                    ))
                    .collect(Collectors.toList());
        }

        String cancelledBy = null;
        if (ride.getCancelledBy() != null) {
            cancelledBy = ride.getCancelledBy().name().toLowerCase();
        }

        String rideDate = ride.getStartTime() != null
                ? ride.getStartTime().format(DATE_FORMATTER)
                : "";

        return CompletedRideDTO.builder()
                .id(ride.getId())
                .passengerName(passengerName)
                .pickupAddress(ride.getStartAddress())
                .dropoffAddress(ride.getEndAddress())
                .rideDate(rideDate)
                .totalCost(ride.getTotalCost())
                .cancelledBy(cancelledBy)
                .panicActivation(ride.getPanicActivated() != null ? ride.getPanicActivated() : false)
                .feedback(feedback)
                .linkedPassengers(linkedPassengerNames)
                .pickupCoords(pickupCoords)
                .dropoffCoords(dropoffCoords)
                .waypoints(waypoints)
                .build();
    }
}