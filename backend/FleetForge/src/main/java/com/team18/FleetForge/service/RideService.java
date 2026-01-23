package com.team18.FleetForge.service;

import com.team18.FleetForge.dto.driver.DriverRideHistoryDTO;
import com.team18.FleetForge.dto.ride.lifecycle.RideCreateRequestDTO;
import com.team18.FleetForge.dto.ride.view.RideDetailsDTO;
import com.team18.FleetForge.model.ride.Ride;

import java.time.LocalDate;
import java.util.List;

public interface RideService {

    List<DriverRideHistoryDTO> getDriverRideHistory(Long driverId, LocalDate startDate);

    RideDetailsDTO getRideDetails(Long rideId);

    List<DriverRideHistoryDTO> getAllDriverRides(Long driverId);

    boolean hasActiveRides(Long driverId);

    DriverRideHistoryDTO getCurrentActiveRide(Long driverId);

    Ride createRide(RideCreateRequestDTO rideCreateRequestDTO);

    Ride getRideById(Long rideId);
}