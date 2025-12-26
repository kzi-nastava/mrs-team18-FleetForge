package com.team18.FleetForge.controller;

import com.team18.FleetForge.dto.ride.lifecycle.RideCancellationRequestDTO;
import com.team18.FleetForge.dto.ride.lifecycle.RideCancellationResponseDTO;
import com.team18.FleetForge.dto.ride.lifecycle.RideEndRequestDTO;
import com.team18.FleetForge.dto.ride.lifecycle.RideEndResponseDTO;
import com.team18.FleetForge.model.enums.RideCancellationRole;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/rides")
public class RideController {

    /**
     * POST /api/rides/{rideId}/cancel
     * Request Body:
     *  - cancelledBy (DRIVER | PASSENGER)
     *  - reason (String, required for DRIVER)
     *  - scheduledStartTime (LocalDateTime)
     * Response:
     *  - success (boolean)
     *  - message (String)
     */
    @PostMapping(
            value = "/{rideId}/cancel",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<RideCancellationResponseDTO> cancelRide(
            @PathVariable Long rideId,
            @RequestBody RideCancellationRequestDTO request
    ) {
        LocalDateTime now = LocalDateTime.now();

        if (request.getCancelledBy() == RideCancellationRole.PASSENGER) {
            long minutesUntilStart =
                    Duration.between(now, request.getScheduledStartTime()).toMinutes();

            if (minutesUntilStart < 10) {
                return new ResponseEntity<>(
                        new RideCancellationResponseDTO(
                                false,
                                "Passenger cancellation allowed only 10 minutes before ride start."
                        ),
                        HttpStatus.BAD_REQUEST
                );
            }
        }

        if (request.getCancelledBy() == RideCancellationRole.DRIVER &&
                (request.getReason() == null || request.getReason().isBlank())) {
            return new ResponseEntity<>(
                    new RideCancellationResponseDTO(
                            false,
                            "Driver must provide a cancellation reason."
                    ),
                    HttpStatus.BAD_REQUEST
            );
        }

        return new ResponseEntity<>(
                new RideCancellationResponseDTO(
                        true,
                        "Ride " + rideId + " successfully cancelled."
                ),
                HttpStatus.OK
        );
    }

    /**
     * POST /api/rides/{rideId}/early-end
     * Request Body:
     *  - stopLocation (GeoPoint)
     *  - endTime (LocalDateTime)
     * Response:
     *  - finalDestination (GeoPoint)
     *  - finalPrice (double)
     */
    @PostMapping(
            value = "/{rideId}/early-end",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<RideEndResponseDTO> endRide(
            @PathVariable Long rideId,
            @RequestBody RideEndRequestDTO request
    ) {
        // Dummy recalculation logic
        double recalculatedPrice = 620.00;

        RideEndResponseDTO response = RideEndResponseDTO.builder()
                .finalDestination(request.getStopLocation())
                .finalPrice(recalculatedPrice)
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
