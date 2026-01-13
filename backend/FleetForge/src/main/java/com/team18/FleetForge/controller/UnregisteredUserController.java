package com.team18.FleetForge.controller;

import com.team18.FleetForge.dto.vehicle.VehicleLocationDTO;
import com.team18.FleetForge.service.VehicleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/unregistered-users")
@RequiredArgsConstructor
@Slf4j
public class UnregisteredUserController {

    private final VehicleService vehicleService;

    @GetMapping("/active-vehicles")
    public ResponseEntity<List<VehicleLocationDTO>> getActiveVehicles() {
        log.info("Request received to get all active vehicles");

        List<VehicleLocationDTO> vehicles = vehicleService.getActiveVehicleLocations();

        log.info("Returning {} active vehicles", vehicles.size());

        return ResponseEntity.ok(vehicles);
    }
}