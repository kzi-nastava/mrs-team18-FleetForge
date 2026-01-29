package com.team18.FleetForge.controller.ride;

import com.team18.FleetForge.dto.ride.lifecycle.*;

import com.team18.FleetForge.dto.ride.panic.RidePanicResponseDTO;
import com.team18.FleetForge.model.ride.GeoPoint;
import com.team18.FleetForge.model.ride.Ride;
import com.team18.FleetForge.service.rides.RideCancellationService;
import com.team18.FleetForge.service.rides.RidePanicService;
import com.team18.FleetForge.service.rides.RideService;
import jakarta.validation.Valid;
import com.team18.FleetForge.service.rides.RideCancellationService;
import com.team18.FleetForge.service.rides.RidePanicService;
import com.team18.FleetForge.service.rides.RideService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;


import java.time.LocalDateTime;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/rides")
public class RideController {

    private final RideService rideService;
    private final RideCancellationService rideCancellationService;
    private final RidePanicService ridePanicService;


    /**
     * POST /api/rides/{rideId}/cancellations
     * Request Body:
     *  - cancelledBy (DRIVER | PASSENGER)
     *  - reason (String, required for DRIVER)
     * Response:
     *  - success (boolean)
     *  - message (String)
     */
    @PreAuthorize("hasRole('DRIVER') or hasRole('PASSENGER')")
    @PostMapping(
            value = "/{rideId}/cancellations",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<RideCancellationResponseDTO> cancelRide(
            @PathVariable Long rideId,
            @RequestBody RideCancellationRequestDTO request,
            Authentication authentication
    ) {
        RideCancellationResult result =
                rideCancellationService.cancelRide(rideId, request, authentication);

        return ResponseEntity
                .status(result.getHttpStatus())
                .body(new RideCancellationResponseDTO(
                        result.isSuccess(),
                        result.getMessage()
                ));
    }

    /**
     * POST /api/rides/{rideId}/panic
     * Response:
     *  - success (boolean)
     *  - message (String)
     */
    @PreAuthorize("hasRole('DRIVER') or hasRole('PASSENGER')")
    @PostMapping("/{rideId}/panic")
    public ResponseEntity<RidePanicResponseDTO> triggerPanic(
            @PathVariable Long rideId,
            Authentication authentication
    ) {
        RidePanicResponseDTO response = ridePanicService.triggerPanic(rideId, authentication);
        return ResponseEntity
                .status(response.isSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST)
                .body(response);
    }
    @PreAuthorize("hasRole('PASSENGER')")
    @PostMapping("/create")
    public ResponseEntity<RideCreateResponseDTO> createRide(@Valid @RequestBody RideCreateRequestDTO request) {
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

}
