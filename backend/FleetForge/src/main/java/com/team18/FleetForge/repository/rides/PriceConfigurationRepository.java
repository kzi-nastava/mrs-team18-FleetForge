package com.team18.FleetForge.repository.rides;

import com.team18.FleetForge.model.enums.VehicleType;
import com.team18.FleetForge.model.ride.PriceConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PriceConfigurationRepository extends JpaRepository<PriceConfiguration, Long> {

    Optional<PriceConfiguration> findByVehicleType(VehicleType vehicleType);
}