package com.team18.FleetForge.service.users;

import com.team18.FleetForge.model.ride.Ride;
import com.team18.FleetForge.model.users.Driver;
import com.team18.FleetForge.model.users.DriverSession;

import java.util.List;

public interface DriverService {
    Driver findAvailableDriver(Ride ride);
    void setDriversScheduledRides();
    boolean checkAlreadyBookedDateTime(Ride ride);
    Long checkDriverActivityProfile(List<DriverSession> sessions);
}
