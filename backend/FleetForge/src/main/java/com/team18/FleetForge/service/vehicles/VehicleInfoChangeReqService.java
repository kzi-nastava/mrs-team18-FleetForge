package com.team18.FleetForge.service.vehicles;

import com.team18.FleetForge.model.vecihles.VehicleInformationChangeRequest;
import com.team18.FleetForge.model.users.Driver;

import java.util.List;

public interface VehicleInfoChangeReqService {

    VehicleInformationChangeRequest findById(Long id);
    void save(VehicleInformationChangeRequest request);
    List<VehicleInformationChangeRequest> findAllPending();
    Driver getDriverForVehicleRequest(Long vehicleId);
}
