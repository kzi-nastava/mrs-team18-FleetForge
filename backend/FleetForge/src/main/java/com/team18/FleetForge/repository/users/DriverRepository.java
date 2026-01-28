package com.team18.FleetForge.repository.users;

import com.team18.FleetForge.model.users.Driver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface DriverRepository extends JpaRepository<Driver, Long> {


    // Find all drivers who are currently active in the system.
    @Query("SELECT d FROM Driver d WHERE d.isActive = true")
    List<Driver> findAllActiveDrivers();


     // Find all drivers who are both active and available for rides.
    @Query("SELECT d FROM Driver d WHERE d.isActive = true AND d.isAvailable = true")
    List<Driver> findAllActiveAndAvailableDrivers();

    // Find a driver by email address.
    Driver findByEmail(String email);

    Driver findByVehicleId(Long vehicleId);

    List<Driver> findByIsAvailableTrue();

    List<Driver> findByIsActiveTrue();
}