package com.team18.FleetForge.service;

import com.team18.FleetForge.model.DriverSession;
import com.team18.FleetForge.model.users.Driver;

import java.util.List;

public interface DriverSessionService {
    List<DriverSession> findAllDriverSessions(Driver driver);
    void save(DriverSession driverSession);
    DriverSession findBySessionById(Long sessionId);
}
