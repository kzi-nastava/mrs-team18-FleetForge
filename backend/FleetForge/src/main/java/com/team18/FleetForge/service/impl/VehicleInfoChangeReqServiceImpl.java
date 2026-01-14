package com.team18.FleetForge.service.impl;

import com.team18.FleetForge.model.Vehicle;
import com.team18.FleetForge.model.VehicleInformationChangeRequest;
import com.team18.FleetForge.model.enums.InformationChangeRequestStatus;
import com.team18.FleetForge.repository.VehicleInfoChangeReqRepo;
import com.team18.FleetForge.service.VehicleInfoChangeReqService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VehicleInfoChangeReqServiceImpl implements VehicleInfoChangeReqService {
    private final VehicleInfoChangeReqRepo repo;
    @Override
    public VehicleInformationChangeRequest findById(Long id) {
        return repo.findById(id).orElseThrow(() -> new RuntimeException("Request not found"));
    }

    @Override
    public void save(VehicleInformationChangeRequest request) {
        repo.save(request);
    }

    @Override
    public List<VehicleInformationChangeRequest> findAllPending() {
        return repo.findAllByStatus(InformationChangeRequestStatus.PENDING);
    }
}
