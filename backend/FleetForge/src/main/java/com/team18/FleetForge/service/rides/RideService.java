package com.team18.FleetForge.service.rides;

import com.team18.FleetForge.dto.driver.DriverRideHistoryDTO;
import com.team18.FleetForge.dto.ride.view.*;
import com.team18.FleetForge.dto.ride.lifecycle.RideCreateRequestDTO;
import com.team18.FleetForge.model.ride.Ride;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface RideService {

    List<DriverRideHistoryDTO> getDriverRideHistory(Long driverId, LocalDate startDate);

    RideDetailsDTO getRideDetails(Long rideId);

    List<DriverRideHistoryDTO> getAllDriverRides(Long driverId);

    boolean hasActiveRides(Long driverId);

    DriverRideHistoryDTO getCurrentActiveRide(Long driverId);

    Ride createRide(RideCreateRequestDTO rideCreateRequestDTO);

    Ride getRideById(Long rideId);

    PassengerRideDetailsDTO getPassengerRideDetails(Long passengerId, Long rideId);

    Page<PassengerRideHistoryDto> getPassengerRideHistory(
            Long passengerId,
            LocalDateTime from,
            LocalDateTime to,
            String sortBy,
            String direction,
            int page,
            int size
    );

    Page<AdminRideHistoryDTO> getAdminRideHistory(
            Long userId,
            String email,
            LocalDateTime from,
            LocalDateTime to,
            String sortBy,
            String direction,
            int page,
            int size
    );

    AdminRideDetailsDTO getAdminRideDetails(Long rideId);
}