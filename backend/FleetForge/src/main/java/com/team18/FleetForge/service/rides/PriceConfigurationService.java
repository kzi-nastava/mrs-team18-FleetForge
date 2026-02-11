package com.team18.FleetForge.service.rides;

import com.team18.FleetForge.dto.ride.estimate.PriceConfigurationDTO;
import com.team18.FleetForge.dto.ride.estimate.UpdatePriceConfigurationDTO;
import com.team18.FleetForge.model.enums.VehicleType;
import com.team18.FleetForge.model.ride.PriceConfiguration;
import com.team18.FleetForge.repository.rides.PriceConfigurationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PriceConfigurationService {

    private final PriceConfigurationRepository priceConfigurationRepository;

    @Transactional(readOnly = true)
    public List<PriceConfigurationDTO> getAllPriceConfigurations() {
        return priceConfigurationRepository.findAll().stream()
                .map(this::buildPriceConfigurationDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PriceConfigurationDTO getPriceConfigurationByVehicleType(VehicleType vehicleType) {
        PriceConfiguration config = priceConfigurationRepository.findByVehicleType(vehicleType)
                .orElseThrow(() -> new RuntimeException(
                        "Price configuration not found for vehicle type: " + vehicleType));
        return buildPriceConfigurationDTO(config);
    }

    @Transactional
    public PriceConfigurationDTO updatePriceConfiguration(VehicleType vehicleType,
                                                          UpdatePriceConfigurationDTO dto) {
        PriceConfiguration config = priceConfigurationRepository.findByVehicleType(vehicleType)
                .orElseThrow(() -> new RuntimeException(
                        "Price configuration not found for vehicle type: " + vehicleType));

        config.setBasePrice(dto.getBasePrice());
        config.setPricePerKm(dto.getPricePerKm());

        PriceConfiguration updated = priceConfigurationRepository.save(config);
        return buildPriceConfigurationDTO(updated);
    }

    @Transactional(readOnly = true)
    public double getBasePriceForVehicleType(VehicleType vehicleType) {
        return priceConfigurationRepository.findByVehicleType(vehicleType)
                .map(PriceConfiguration::getBasePrice)
                .orElseGet(() -> vehicleType.getBasePrice());
    }

    @Transactional(readOnly = true)
    public double getPricePerKmForVehicleType(VehicleType vehicleType) {
        return priceConfigurationRepository.findByVehicleType(vehicleType)
                .map(PriceConfiguration::getPricePerKm)
                .orElse(120.0); // Fallback to default
    }

    private PriceConfigurationDTO buildPriceConfigurationDTO(PriceConfiguration config) {
        PriceConfigurationDTO dto = new PriceConfigurationDTO();
        dto.setId(config.getId());
        dto.setVehicleType(config.getVehicleType());
        dto.setBasePrice(config.getBasePrice());
        dto.setPricePerKm(config.getPricePerKm());
        return dto;
    }
}