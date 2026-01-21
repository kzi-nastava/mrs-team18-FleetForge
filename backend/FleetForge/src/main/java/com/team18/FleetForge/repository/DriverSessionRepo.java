package com.team18.FleetForge.repository;

import com.team18.FleetForge.model.DriverSession;
import com.team18.FleetForge.model.users.Driver;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DriverSessionRepo extends JpaRepository<DriverSession,Long> {
    List<DriverSession> findByDriver(Driver driver);
    Optional<DriverSession> findById(Long sessionId);
}
