package com.team18.FleetForge.service.rides;

import com.team18.FleetForge.dto.ride.panic.RidePanicResponseDTO;
import com.team18.FleetForge.model.enums.RideActor;
import com.team18.FleetForge.model.enums.RideStatus;
import com.team18.FleetForge.model.ride.Ride;
import com.team18.FleetForge.repository.rides.RideRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class RidePanicService {

    private final RideRepository rideRepository;

    public RidePanicResponseDTO triggerPanic(
            Long rideId,
            Authentication authentication
    ) {
        Ride ride = rideRepository.findById(rideId).orElse(null);
        if (ride == null) {
            return new RidePanicResponseDTO(
                    false,
                    "Ride does not exist"
            );
        }

        if (ride.getStatus() != RideStatus.IN_PROGRESS) {
            return new RidePanicResponseDTO(
                    false,
                    "Panic can only be triggered for rides in progress"
            );
        }

        if (Boolean.TRUE.equals(ride.getPanicActivated())) {
            return new RidePanicResponseDTO(
                    false,
                    "Panic has already been activated for this ride"
            );
        }

        Object principal = authentication.getPrincipal();
        RideActor initiator;
        if (principal instanceof com.team18.FleetForge.model.users.Driver) {
            initiator = RideActor.DRIVER;
        } else if (principal instanceof com.team18.FleetForge.model.users.Passenger) {
            initiator = RideActor.PASSENGER;
        } else {
            initiator = null;
        }

        ride.setPanicActivated(true);
        ride.setPanicActivatedAt(LocalDateTime.now());
        ride.setPanicInitiator(initiator);

        rideRepository.save(ride);

        return new RidePanicResponseDTO(
                true,
                "Panic successfully activated"
        );
    }

}
