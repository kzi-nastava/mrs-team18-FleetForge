package com.team18.FleetForge.controller;

import com.team18.FleetForge.dto.vehicle.VehicleLocationDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/unregistered")
public class UnregisteredUserController {

    // TODO: Inject VehicleService when implemented


    @GetMapping("/vehicles/active")
    public ResponseEntity<List<VehicleLocationDTO>> getActiveVehicles() {
        // TODO: Implement service call
        // List<VehicleLocationDTO> vehicles = vehicleService.getActiveVehicleLocations();
        // return ResponseEntity.ok(vehicles);

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}