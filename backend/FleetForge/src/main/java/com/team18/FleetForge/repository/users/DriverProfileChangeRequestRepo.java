package com.team18.FleetForge.repository.users;

import com.team18.FleetForge.model.users.DriverProfileChangeRequest;
import com.team18.FleetForge.model.enums.InformationChangeRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DriverProfileChangeRequestRepo extends JpaRepository<DriverProfileChangeRequest, Long> {
    List<DriverProfileChangeRequest> findAllByStatus(InformationChangeRequestStatus status);
}
