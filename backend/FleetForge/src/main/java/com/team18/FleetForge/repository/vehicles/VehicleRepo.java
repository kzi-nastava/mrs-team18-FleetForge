package com.team18.FleetForge.repository.vehicles;

import com.team18.FleetForge.model.vecihles.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VehicleRepo extends JpaRepository<Vehicle, Long> {
}
