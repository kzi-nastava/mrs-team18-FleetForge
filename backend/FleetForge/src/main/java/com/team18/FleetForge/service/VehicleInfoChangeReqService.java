package com.team18.FleetForge.service;

import com.team18.FleetForge.model.Vehicle;
import com.team18.FleetForge.model.VehicleInformationChangeRequest;
import com.team18.FleetForge.model.users.Driver;

import java.util.List;

public interface VehicleInfoChangeReqService {

    VehicleInformationChangeRequest findById(Long id);
    void save(VehicleInformationChangeRequest request);
    List<VehicleInformationChangeRequest> findAllPending();
    Driver getDriverForVehicleRequest(Long vehicleId);
}
