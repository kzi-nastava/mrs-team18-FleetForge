package com.team18.FleetForge.service.users;

import com.team18.FleetForge.model.users.DriverProfileChangeRequest;

import java.util.List;

public interface DriverProfileChangeRequestService {
    void save(DriverProfileChangeRequest driverProfileChangeRequest);
    DriverProfileChangeRequest findById(Long id);
    List<DriverProfileChangeRequest> findAllPending();
}
