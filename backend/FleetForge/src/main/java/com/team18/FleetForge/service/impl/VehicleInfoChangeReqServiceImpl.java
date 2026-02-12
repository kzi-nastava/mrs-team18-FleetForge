package com.team18.FleetForge.service.impl;

import com.team18.FleetForge.model.vehicles.VehicleInformationChangeRequest;
import com.team18.FleetForge.model.enums.InformationChangeRequestStatus;
import com.team18.FleetForge.model.users.Driver;
import com.team18.FleetForge.repository.users.DriverRepository;
import com.team18.FleetForge.repository.vehicles.VehicleInfoChangeReqRepo;
import com.team18.FleetForge.service.vehicles.VehicleInfoChangeReqService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VehicleInfoChangeReqServiceImpl implements VehicleInfoChangeReqService {
    private final VehicleInfoChangeReqRepo repo;
    private final DriverRepository driverRepository;
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

    @Override
    public Driver getDriverForVehicleRequest(Long vehicleId) {
        return driverRepository.findByVehicleId(vehicleId);
    }
}
