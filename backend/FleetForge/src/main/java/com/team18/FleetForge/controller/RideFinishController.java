package com.team18.FleetForge.controller;

import com.team18.FleetForge.dto.ride.lifecycle.FinishRideRequestDTO;
import com.team18.FleetForge.dto.ride.lifecycle.FinishRideResponseDTO;
import com.team18.FleetForge.service.RideFinishService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rides")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class RideFinishController {

    private final RideFinishService rideFinishService;

    /**
     * Endpoint for drivers to mark a ride as completed
     * POST /api/rides/finish
     */
    @PostMapping("/finish")
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<FinishRideResponseDTO> finishRide(
            @Valid @RequestBody FinishRideRequestDTO request) {

        try {
            FinishRideResponseDTO response = rideFinishService.finishRide(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(FinishRideResponseDTO.builder()
                            .message("Error: " + e.getMessage())
                            .build());
        }
    }
}