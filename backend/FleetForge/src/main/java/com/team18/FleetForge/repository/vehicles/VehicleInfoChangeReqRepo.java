package com.team18.FleetForge.repository.vehicles;

import com.team18.FleetForge.model.vecihles.VehicleInformationChangeRequest;
import com.team18.FleetForge.model.enums.InformationChangeRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VehicleInfoChangeReqRepo extends JpaRepository<VehicleInformationChangeRequest, Long> {
    List<VehicleInformationChangeRequest> findAllByStatus(InformationChangeRequestStatus status);
}
