package com.team18.FleetForge.service;

import com.team18.FleetForge.model.ride.Ride;
import com.team18.FleetForge.model.users.Driver;

public interface DriverService {
    Driver findAvailableDriver(Ride ride);
    void setDriversScheduledRides();
}
