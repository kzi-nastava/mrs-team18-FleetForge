package com.team18.FleetForge.controller.ride;

import com.team18.FleetForge.dto.ride.estimate.RideEstimateRequestDTO;
import com.team18.FleetForge.dto.ride.estimate.RideEstimateResponseDTO;
import com.team18.FleetForge.model.enums.VehicleType;
import com.team18.FleetForge.service.PriceCalculationService;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/api/ride-estimates")
public class RideEstimateController {

    private final PriceCalculationService priceCalculationService;

    /**
     * POST /api/ride-estimates
     * Request Body:
     *  - distanceKm (double)
     * Response:
     *  - estimatedPrice (double)
     */
    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<RideEstimateResponseDTO> estimateRide(
            @RequestBody RideEstimateRequestDTO request
    ) {
        double price = priceCalculationService.calculatePrice(
                request.getDistanceKm(),
                VehicleType.STANDARD
        );
        RideEstimateResponseDTO response = new RideEstimateResponseDTO();
        response.setEstimatedPrice(price);
        return ResponseEntity.ok(response);
    }
}
