package com.team18.FleetForge.service.rides;

import com.team18.FleetForge.dto.ride.view.ActiveRideDTO;
import com.team18.FleetForge.dto.ride.view.ActiveRideDetailsDTO;
import com.team18.FleetForge.model.enums.RideStatus;
import com.team18.FleetForge.model.ride.Ride;
import com.team18.FleetForge.model.users.Driver;
import com.team18.FleetForge.model.users.Passenger;
import com.team18.FleetForge.repository.rides.RideRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ActiveRideService {

    private final RideRepository rideRepository;

    @Transactional(readOnly = true)
    public List<ActiveRideDTO> getAllActiveRides() {
        List<Ride> activeRides = rideRepository.findAllByStatus(RideStatus.IN_PROGRESS);

        return activeRides.stream()
                .map(this::buildActiveRideDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ActiveRideDetailsDTO getActiveRideLiveData(Long rideId) {
        Optional<Ride> rideOpt = rideRepository.findById(rideId);

        if (rideOpt.isEmpty()) {
            return null;
        }

        Ride ride = rideOpt.get();

        if (ride.getStatus() != RideStatus.IN_PROGRESS) {
            return null;
        }

        return buildActiveRideDetailsDTO(ride);
    }


    private ActiveRideDTO buildActiveRideDTO(Ride ride) {
        Driver driver = ride.getDriver();
        
        int passengerCount = 1; 
        if (ride.getLinkedPassengers() != null) {
            passengerCount += ride.getLinkedPassengers().size();
        }

        return ActiveRideDTO.builder()
                .rideId(ride.getId())
                .driverFirstName(driver.getFirstName())
                .driverLastName(driver.getLastName())
                .driverProfileImage(driver.getProfilePicture())
                .startAddress(ride.getStartAddress())
                .endAddress(ride.getEndAddress())
                .startTime(ride.getStartTime())
                .panicActivated(ride.getPanicActivated())
                .passengerCount(passengerCount)
                .build();
    }
    
    private ActiveRideDetailsDTO buildActiveRideDetailsDTO(Ride ride) {
        Driver driver = ride.getDriver();
        
        Passenger primaryPassenger = ride.getPassenger();
        List<ActiveRideDetailsDTO.PassengerDTO> passengers = new ArrayList<>();

        if (primaryPassenger != null) {
            passengers.add(buildPassengerDTO(primaryPassenger));
        }

        // Map linked passengers
        if (ride.getLinkedPassengers() != null && !ride.getLinkedPassengers().isEmpty()) {
            ride.getLinkedPassengers().stream()
                    .map(this::buildPassengerDTO)
                    .forEach(passengers::add);
        }

        return ActiveRideDetailsDTO.builder()
                .rideId(ride.getId())
                // Driver information
                .driverFirstName(driver.getFirstName())
                .driverLastName(driver.getLastName())
                .driverPhoneNumber(driver.getPhoneNumber())
                .driverProfileImage(driver.getProfilePicture())
                // LIVE location - this is updated by driver every 2 seconds
                .currentLocation(driver.getCurrentLocation())
                // Ride route with full coordinates
                .startLocation(ride.getStartLocation())
                .startAddress(ride.getStartAddress())
                .endLocation(ride.getEndLocation())
                .endAddress(ride.getEndAddress())
                .vehicleType(driver.getVehicle().getType())
                // Timing
                .startTime(ride.getStartTime())
                // Status
                .panicActivated(ride.getPanicActivated())
                .panicActivatedAt(ride.getPanicActivatedAt())
                // Full passenger details
                .passengers(passengers)
                .build();
    }

    private ActiveRideDetailsDTO.PassengerDTO buildPassengerDTO(Passenger passenger) {
        return ActiveRideDetailsDTO.PassengerDTO.builder()
                .firstName(passenger.getFirstName())
                .lastName(passenger.getLastName())
                .email(passenger.getEmail())
                .build();
    }
}