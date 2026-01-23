package com.team18.FleetForge.controller;

import com.team18.FleetForge.dto.ride.lifecycle.*;

import com.team18.FleetForge.dto.ride.review.RideReviewRequestDTO;
import com.team18.FleetForge.dto.ride.review.RideReviewResponseDTO;
import com.team18.FleetForge.model.GeoPoint;
import com.team18.FleetForge.model.Route;
import com.team18.FleetForge.model.enums.RideCancellationRole;
import com.team18.FleetForge.model.enums.RideStatus;
import com.team18.FleetForge.model.ride.Ride;
import com.team18.FleetForge.service.RideService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.security.Principal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/rides")
public class RideController {

    private final RideService rideService;


    /**
     * POST /api/rides/{rideId}/cancellations
     * Request Body:
     *  - cancelledBy (DRIVER | PASSENGER)
     *  - reason (String, required for DRIVER)
     *  - scheduledStartTime (LocalDateTime)
     * Response:
     *  - success (boolean)
     *  - message (String)
     */
    @PostMapping(
            value = "/{rideId}/cancellations",
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


    @PostMapping("/create")
    public ResponseEntity<RideCreateResponseDTO> createRide(@RequestBody RideCreateRequestDTO request) {
        Ride ride=rideService.createRide(request);
        RideCreateResponseDTO response= new RideCreateResponseDTO();
        if(ride==null){
            response.setCreated(false);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        response.setCreated(true);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

//    Authentication authentication = authenticationManager.authenticate(
//            new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword())
//    );
//
//SecurityContextHolder.getContext().setAuthentication(authentication);


    /**
     * PUT /api/rides/{rideId}/complete
     * Response:
     *  - rideId (Long)
     *  - status (String) - "COMPLETED"
     *  - completedAt (LocalDateTime)
     *  - finalPrice (Double)
     *  - message (String)
     *  - nextRide (NextRideDTO, optional) - if driver has next scheduled ride
     */
    @PutMapping(
            value = "/{rideId}/complete",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<RideCompletionResponseDTO> completeRide(@PathVariable Long rideId) {
        // Dummy completion data with next scheduled ride
        RideCompletionResponseDTO response = RideCompletionResponseDTO.builder()
                .rideId(rideId)
                .status("COMPLETED")
                .completedAt(LocalDateTime.now())
                .finalPrice(1450.00)
                .message("Ride completed successfully. Driver is now available.")
                .nextRide(RideCompletionResponseDTO.NextRideDTO.builder()
                        .rideId(456L)
                        .startLocation(new GeoPoint(45.2550, 19.8450))
                        .startAddress("Bulevar oslobođenja 46, Novi Sad")
                        .endLocation(new GeoPoint(45.2671, 19.8335))
                        .endAddress("Trg slobode 1, Novi Sad")
                        .scheduledFor(LocalDateTime.now().plusMinutes(30))
                        .estimatedDurationMinutes(15)
                        .passenger(RideCompletionResponseDTO.NextRideDTO.PassengerInfoDTO.builder()
                                .id(25L)
                                .firstName("Ana")
                                .lastName("Anić")
                                .phoneNumber("+381649876543")
                                .profileImage("passenger25.jpg")
                                .build())
                        .build())
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/{id}/start")
    public ResponseEntity<RideStartResponseDTO> startRide(@PathVariable Long id) {
        RideStartResponseDTO responseDTO = new RideStartResponseDTO();
        responseDTO.setId(id);
        responseDTO.setStatus(RideStatus.IN_PROGRESS);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }
}
