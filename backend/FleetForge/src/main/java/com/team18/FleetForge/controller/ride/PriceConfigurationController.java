package com.team18.FleetForge.controller.ride;

import com.team18.FleetForge.dto.ride.estimate.PriceConfigurationDTO;
import com.team18.FleetForge.dto.ride.estimate.UpdatePriceConfigurationDTO;
import com.team18.FleetForge.model.enums.VehicleType;
import com.team18.FleetForge.service.rides.PriceConfigurationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/price-configurations")
@RequiredArgsConstructor
public class PriceConfigurationController {

    private final PriceConfigurationService priceConfigurationService;

    /**
     * Get all price configurations
     * Accessible by: ADMIN
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PriceConfigurationDTO>> getAllPriceConfigurations() {
        List<PriceConfigurationDTO> configurations = priceConfigurationService.getAllPriceConfigurations();
        return ResponseEntity.ok(configurations);
    }

    /**
     * Get price configuration by vehicle type
     * Accessible by: ADMIN
     */
    @GetMapping("/{vehicleType}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PriceConfigurationDTO> getPriceConfigurationByVehicleType(
            @PathVariable VehicleType vehicleType) {
        PriceConfigurationDTO configuration = priceConfigurationService
                .getPriceConfigurationByVehicleType(vehicleType);
        return ResponseEntity.ok(configuration);
    }

    /**
     * Update existing price configuration
     * Accessible by: ADMIN
     */
    @PutMapping("/{vehicleType}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PriceConfigurationDTO> updatePriceConfiguration(
            @PathVariable VehicleType vehicleType,
            @Valid @RequestBody UpdatePriceConfigurationDTO dto) {
        PriceConfigurationDTO updated = priceConfigurationService
                .updatePriceConfiguration(vehicleType, dto);
        return ResponseEntity.ok(updated);
    }

}