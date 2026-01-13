package com.team18.FleetForge.service;

import com.team18.FleetForge.dto.vehicle.VehicleLocationDTO;

import java.util.List;

public interface VehicleService {

    List<VehicleLocationDTO> getActiveVehicleLocations();
}