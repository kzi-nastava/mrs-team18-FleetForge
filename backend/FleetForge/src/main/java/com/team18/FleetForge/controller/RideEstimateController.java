package com.team18.FleetForge.controller;


import com.team18.FleetForge.dto.RideEstimateRequestDTO;
import com.team18.FleetForge.dto.RideEstimateResponseDTO;
import com.team18.FleetForge.dto.RouteDTO;
import com.team18.FleetForge.model.GeoPoint;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ride-estimates")
public class RideEstimateController {

    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<RideEstimateResponseDTO> estimateRide(
            @RequestBody RideEstimateRequestDTO request
    ) {
        // Dummy geometry (as returned by OSM-based routers)
        List<GeoPoint> geometry = List.of(
                request.getStartLocation(),
                new GeoPoint(45.2685, 19.8400),
                new GeoPoint(45.2700, 19.8500),
                request.getDestinationLocation()
        );

        RouteDTO route = new RouteDTO();
        route.setGeometry(geometry);
        route.setDistanceMeters(5200);
        route.setDurationSeconds(900);

        RideEstimateResponseDTO response = new RideEstimateResponseDTO();
        response.setRoute(route);
        response.setEstimatedCost(780.00);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}