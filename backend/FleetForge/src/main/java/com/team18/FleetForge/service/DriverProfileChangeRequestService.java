package com.team18.FleetForge.service;

import com.team18.FleetForge.model.DriverProfileChangeRequest;
import com.team18.FleetForge.model.enums.InformationChangeRequestStatus;

import java.util.List;

public interface DriverProfileChangeRequestService {
    void save(DriverProfileChangeRequest driverProfileChangeRequest);
    DriverProfileChangeRequest findById(Long id);
    List<DriverProfileChangeRequest> findAllPending();
}
