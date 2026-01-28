package com.team18.FleetForge.service.impl;

import com.team18.FleetForge.model.users.DriverProfileChangeRequest;
import com.team18.FleetForge.model.enums.InformationChangeRequestStatus;
import com.team18.FleetForge.repository.users.DriverProfileChangeRequestRepo;
import com.team18.FleetForge.service.users.DriverProfileChangeRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DriverProfileChangeRequestServiceImpl implements DriverProfileChangeRequestService {
    private final DriverProfileChangeRequestRepo repo;
    @Override
    public void save(DriverProfileChangeRequest driverProfileChangeRequest) {
        repo.save(driverProfileChangeRequest);
    }

    @Override
    public DriverProfileChangeRequest findById(Long id) {
        return  repo.findById(id).orElseThrow(() -> new RuntimeException("Request not found"));
    }

    @Override
    public List<DriverProfileChangeRequest> findAllPending() {
        return repo.findAllByStatus(InformationChangeRequestStatus.PENDING);
    }


}
