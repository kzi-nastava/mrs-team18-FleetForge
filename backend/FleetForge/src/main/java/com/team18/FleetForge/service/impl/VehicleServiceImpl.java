package com.team18.FleetForge.service.impl;

import com.team18.FleetForge.dto.vehicle.VehicleLocationDTO;
import com.team18.FleetForge.model.vehicles.Vehicle;
import com.team18.FleetForge.model.users.Driver;
import com.team18.FleetForge.repository.users.DriverRepository;
import com.team18.FleetForge.repository.vehicles.VehicleRepo;
import com.team18.FleetForge.service.vehicles.VehicleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
//Simple Logging Facade for Java
@Slf4j
@Transactional(readOnly = true)
public class VehicleServiceImpl implements VehicleService {

    private final DriverRepository driverRepository;
    private final VehicleRepo  vehicleRepo;

    @Override
    public List<VehicleLocationDTO> getActiveVehicleLocations() {
        log.info("Fetching all active vehicle locations");

        List<Driver> activeDrivers = driverRepository.findAllActiveDrivers();

        log.info("Found {} active drivers", activeDrivers.size());

        return activeDrivers.stream()
                .filter(driver -> driver.getVehicle() != null)
                .map(this::mapDriverToVehicleLocationDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Vehicle findById(Long id) {
        return vehicleRepo.findById(id).orElse(null);
    }

    @Override
    public void save(Vehicle vehicle) {
        vehicleRepo.save(vehicle);
    }

    private VehicleLocationDTO mapDriverToVehicleLocationDTO(Driver driver) {
        Vehicle vehicle = driver.getVehicle();

        return new VehicleLocationDTO(
                vehicle.getId(),
                vehicle.getModel(),
                vehicle.getType(),
                driver.getCurrentLocation(),
                driver.isAvailable(),
                driver.isActive()
        );
    }
}