package com.team18.FleetForge.service.impl;

import com.team18.FleetForge.model.users.DriverSession;
import com.team18.FleetForge.model.users.Driver;
import com.team18.FleetForge.repository.users.DriverSessionRepo;
import com.team18.FleetForge.service.users.DriverSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DriverSessionServiceImpl implements DriverSessionService {
    private final DriverSessionRepo driverSessionRepo;
    @Override
    public List<DriverSession> findAllDriverSessions(Driver driver) {
        return driverSessionRepo.findByDriver(driver);
    }

    @Override
    public void save(DriverSession driverSession) {
        driverSessionRepo.save(driverSession);
    }

    @Override
    public DriverSession findBySessionById(Long sessionId) {
        return  driverSessionRepo.findById(sessionId).orElse(null);
    }
}
