package com.team18.FleetForge.service;

import com.team18.FleetForge.model.Vehicle;
import com.team18.FleetForge.model.VehicleInformationChangeRequest;

import java.util.List;

public interface VehicleInfoChangeReqService {

    VehicleInformationChangeRequest findById(Long id);
    void save(VehicleInformationChangeRequest request);
    List<VehicleInformationChangeRequest> findAllPending();
}
