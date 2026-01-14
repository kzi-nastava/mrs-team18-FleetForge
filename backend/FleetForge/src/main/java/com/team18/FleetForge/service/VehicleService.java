package com.team18.FleetForge.service;

import com.team18.FleetForge.dto.vehicle.VehicleLocationDTO;
import com.team18.FleetForge.model.Vehicle;

import java.util.List;

public interface VehicleService {

    List<VehicleLocationDTO> getActiveVehicleLocations();
    Vehicle findById(Long id);
    void save(Vehicle vehicle);
}