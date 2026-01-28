package com.team18.FleetForge.service.rides;

import com.team18.FleetForge.dto.ride.lifecycle.RideCancellationRequestDTO;
import com.team18.FleetForge.dto.ride.lifecycle.RideCancellationResult;
import com.team18.FleetForge.model.enums.RideStatus;
import com.team18.FleetForge.model.ride.Ride;
import com.team18.FleetForge.repository.rides.RideRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RideCancellationService {

    private final RideRepository rideRepository;

    public RideCancellationResult cancelRide(
            Long rideId,
            RideCancellationRequestDTO request,
            Authentication authentication
    ) {

        Ride ride = rideRepository.findById(rideId).orElse(null);
        if (ride == null) {
            return fail("Ride not found.", HttpStatus.NOT_FOUND);
        }

        if (!isRideCancellable(ride)) {
            return fail("Ride cannot be cancelled in its current state.", HttpStatus.CONFLICT);
        }

        boolean isDriver = hasRole(authentication, "ROLE_DRIVER");
        boolean isPassenger = hasRole(authentication, "ROLE_PASSENGER");

        if (!isDriver && !isPassenger) {
            return fail("Unauthorized cancellation attempt.", HttpStatus.FORBIDDEN);
        }

        if (isDriver) {
            return cancelByDriver(ride, request);
        }

        return cancelByPassenger(ride);
    }

    private RideCancellationResult cancelByDriver(
            Ride ride,
            RideCancellationRequestDTO request
    ) {
        if (request.getReason() == null || request.getReason().isBlank()) {
            return fail(
                    "Driver must provide a cancellation reason.",
                    HttpStatus.BAD_REQUEST
            );
        }

        applyCancellation(ride, request.getReason());
        return success(ride.getId());
    }

    private RideCancellationResult cancelByPassenger(Ride ride) {
        LocalDateTime startTime = ride.getStartTime();
        if (startTime == null) {
            return fail("Ride start time is not defined.", HttpStatus.CONFLICT);
        }

        long minutesUntilStart =
                Duration.between(LocalDateTime.now(), startTime).toMinutes();

        if (minutesUntilStart < 10) {
            return fail(
                    "Passenger can cancel only at least 10 minutes before ride start.",
                    HttpStatus.BAD_REQUEST
            );
        }

        applyCancellation(ride, null);
        return success(ride.getId());
    }

    private boolean isRideCancellable(Ride ride) {
        return ride.getStatus() != RideStatus.CANCELLED
                && ride.getStatus() != RideStatus.COMPLETED
                && ride.getStatus() != RideStatus.IN_PROGRESS;
    }

    private void applyCancellation(Ride ride, String reason) {
        ride.setStatus(RideStatus.CANCELLED);
        ride.setCancelledAt(LocalDateTime.now());
        ride.setCancellationReason(reason);
        rideRepository.save(ride);
    }

    private boolean hasRole(Authentication auth, String role) {
        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(role));
    }

    private RideCancellationResult success(Long rideId) {
        return new RideCancellationResult(
                true,
                "Ride " + rideId + " successfully cancelled.",
                HttpStatus.OK
        );
    }

    private RideCancellationResult fail(String message, HttpStatus status) {
        return new RideCancellationResult(false, message, status);
    }
}
