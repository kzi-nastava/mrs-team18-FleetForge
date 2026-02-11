package com.team18.FleetForge.dto.ride.estimate;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePriceConfigurationDTO {

    @NotNull(message = "Base price is required")
    @Positive(message = "Base price must be positive")
    private Double basePrice;

    @NotNull(message = "Price per kilometer is required")
    @Positive(message = "Price per kilometer must be positive")
    private Double pricePerKm;
}