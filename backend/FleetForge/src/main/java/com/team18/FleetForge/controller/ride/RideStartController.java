package com.team18.FleetForge.controller.ride;

import com.team18.FleetForge.dto.ride.lifecycle.RideStartResponseDTO;
import com.team18.FleetForge.model.enums.RideStatus;
import com.team18.FleetForge.model.ride.Ride;
import com.team18.FleetForge.service.RideService;
import com.team18.FleetForge.service.RideStartService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/rides")
public class RideStartController {
    private final RideStartService service;
    private final RideService rideService;

    @PutMapping("/{id}/start")
    public ResponseEntity<RideStartResponseDTO> startRide(@PathVariable Long id) {
        Ride ride= rideService.getRideById(id);
        return service.startRide(ride);
    }
}
