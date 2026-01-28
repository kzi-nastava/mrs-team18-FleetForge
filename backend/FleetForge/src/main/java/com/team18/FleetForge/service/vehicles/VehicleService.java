package com.team18.FleetForge.service.vehicles;

import com.team18.FleetForge.dto.vehicle.VehicleLocationDTO;
import com.team18.FleetForge.model.vecihles.Vehicle;

import java.util.List;

public interface VehicleService {

    List<VehicleLocationDTO> getActiveVehicleLocations();
    Vehicle findById(Long id);
    void save(Vehicle vehicle);
}