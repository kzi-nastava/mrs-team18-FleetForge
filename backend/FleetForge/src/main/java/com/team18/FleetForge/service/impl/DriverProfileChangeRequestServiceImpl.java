package com.team18.FleetForge.service.impl;

import com.team18.FleetForge.model.DriverProfileChangeRequest;
import com.team18.FleetForge.model.enums.InformationChangeRequestStatus;
import com.team18.FleetForge.repository.DriverProfileChangeRequestRepo;
import com.team18.FleetForge.service.DriverProfileChangeRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
