package com.ognjen.fleetforge.fragments.admin;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.ognjen.fleetforge.dtos.price.PriceConfigurationDTO;
import com.ognjen.fleetforge.dtos.price.UpdatePriceConfigurationDTO;
import com.ognjen.fleetforge.enums.VehicleType;
import com.ognjen.fleetforge.repository.PriceConfigurationRepo;

import java.util.List;

public class PriceManagementViewModel extends ViewModel {
    private final PriceConfigurationRepo repository;

    public PriceManagementViewModel() {
        this.repository = new PriceConfigurationRepo();
    }

    public LiveData<List<PriceConfigurationDTO>> getAllPriceConfigurations() {
        return repository.getAllPriceConfigurations();
    }

    public LiveData<PriceConfigurationDTO> updatePriceConfiguration(VehicleType vehicleType, Double basePrice, Double pricePerKm) {
        UpdatePriceConfigurationDTO dto = new UpdatePriceConfigurationDTO(basePrice, pricePerKm);
        return repository.updatePriceConfiguration(vehicleType, dto);
    }
}

